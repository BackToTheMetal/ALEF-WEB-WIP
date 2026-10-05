/*
 * Copyright 2003-2023 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package jetbrains.mps.util.performance;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Not thread-safe!
 * @author Evgeny Gryaznov
 * @author Artem Tikhomirov
 */
public final class PerformanceTracer implements IPerformanceTracer {
  private static final int MAX_TRACE_DEPTH = 30;
  private int top = 0;
  // stack elements are re-used as we push and pop tasks. Assumption it's unlikely for anyone to get interested in
  // traces deeper than a dozen of nested steps.
  private final StackElement[] myStack = new StackElement[MAX_TRACE_DEPTH];
  private final String traceName;
  private final List<String> externalText;

  public PerformanceTracer(String name) {
    this.traceName = name;
    for (int i = 0; i < myStack.length; i++) {
      myStack[i] = new StackElement();
    }
    // technical element, never pop'ed, serves to hold topmost task
    myStack[0].name = null;
    myStack[0].task = new Task(null); // there's an assumption in toString down here, not to print such a task
    externalText = new ArrayList<>();
  }

  public void push(IPerformanceTracer other) {
    assert other != null;
    if (!(other instanceof PerformanceTracer)) {
      addText(other.report());
      return;
    }
    assert other != this;
    PerformanceTracer ptOther = (PerformanceTracer) other;
    if (ptOther.top != 0) {
      throw new IllegalArgumentException("Another tracer is in incomplete state (still in use?)");
    }
    // tasks of another PT are registered as children of active StackTrace element (we associate it with a task
    // in case there's none yet, e.g. when no completed child SE)
    final Task activeTask = getTask(top);
    ptOther.getTask(0).tasks.forEach(activeTask::addLeaf);
  }

  @Override
  public void nested(IPerformanceTracer other) {
    if (!(other instanceof PerformanceTracer)) {
      addText(other.report());
      return;
    }
    assert other != this;
    PerformanceTracer ptOther = (PerformanceTracer) other;
    if (ptOther.top != 0) {
      throw new IllegalArgumentException("Another tracer is in incomplete state (still in use?)");
    }
    // Here comes important difference from push(PT) -
    // execution time of #nested() tasks is treated as an actual execution time of outer task
    //
    final Task xx = new Task(ptOther.traceName);
    for (Task task : ptOther.getTask(0).tasks) {
      xx.addLeaf(task);
      xx.executionTime += task.executionTime;
    }
    getTask(top).addLeaf(xx);
    // pretend as if the time spend in 'nested' task has been actual execution time for all actual tasks in the stack
    for (int i = top; i > 0; i--) {
      // task[0] is a technical element, we don't update it
      getTask(i).executionTime += xx.executionTime;
    }
  }

  @Override
  public void push(String taskName) {
    top++;
    myStack[top].name = taskName;
    myStack[top].startTime = System.nanoTime();
  }

  @Override
  public void pop() {
    final long end = System.nanoTime();
    final long len = end - myStack[top].startTime;
    String name = myStack[top].name;
    Task wasTask = myStack[top].task;

    if (wasTask == null) {
      // there were no nested StackElements/completed task during push/pop of the actual SE,
      // tell parent that the task of actual SE is over
      getTask(top - 1).addLeaf(name, len);
    } else {
      // there were child SE of this SE that added some tasks to actual SE
      // (iow, SE.task becomes initialized from child SE only, with addLeaf call above)
      wasTask.executionTime += len;
      // used to be assignment instead of += as prior to #nested() we never faced an SE.task with execution time != 0 on SE.pop
      // now that we can face sequence like:
      // t1.push(...); /*some activity*/ t1.nested(t2); t1.pop();
      // there's already a task with an execution time once t1.pop() happens.
    }
    myStack[top].task = null;
    top--;
  }

  private Task getTask(int i) {
    Task t = myStack[i].task;
    if (t == null) {
      t = new Task(myStack[i].name);
      myStack[i].task = t;
      Task parent = getTask(i - 1);
      // there's no need to create tasks for each stack element ancestor if we manage to add
      // existing tasks to parent when StackElement pops. At the moment, however, it's the only
      // place we establish a parent-child relation b/w tasks
      parent.addTask(t);
    }
    return t;
  }

  @Override
  public void addText(String s) {
    externalText.add(s);
  }

  @Override
  public String report(String... separate) {
    return report(0, separate);
  }

  @Override
  public String report(long cutOffTimeMillis, String... separate) {
    if (top == 0) {
      myStack[0].task.merge(new HashSet<>(Arrays.asList(separate)));
      StringBuilder sb = new StringBuilder();
      sb.append('[');
      sb.append(traceName);
      sb.append("]\n");
      myStack[0].task.toString(cutOffTimeMillis, sb, 0);
      for (String s : externalText) {
        sb.append(s);
        sb.append('\n');
      }
      return sb.toString();
    } else {
      return null;
    }
  }

  private static class StackElement {
    String name;
    // we keep nanosecond values though it's unlikely we need such precision
    long startTime;
    // field is initialized the moment child stack element pops and adds its stats as a new task to
    // a task of its parent StackElement.
    Task task;
  }

  private static class Task {
    final String name;
    long executionTime; // in nanoseconds. OWN execution time of the task, excluding children tasks
    int invocationCount;
    final List<Task> tasks = new ArrayList<>(5);

    /*package*/ Task(String name) {
      this.name = name;
      this.executionTime = 0;
      this.invocationCount = 1;
    }

    // XXX it's not apparent whether it's reasonable to merge tasks the moment leaf is added, or to add
    //     new tasks into a list and merge them at parent's SE pop() call.
    /*package*/ Task addLeaf(String name, long time) {
      // XXX not apparent if there's solid reason to look tasks up instead of simply
      //     creating a lot of them and then merge at report time
      Task t = null;
      for (Task ts : tasks) {
        if (name.equals(ts.name)) {
          t = ts;
          t.invocationCount++;
          break;
        }
      }
      if (t == null) {
        t = new Task(name);
        tasks.add(t);
      }
      t.executionTime += time;
      return t;
    }

    /*package*/ void addLeaf(Task another) {
      Task t = addLeaf(another.name, another.executionTime);
      another.tasks.forEach(t::addLeaf);
    }

    /*package*/ void addTask(Task task) {
      tasks.add(task);
    }

    public void merge(Set<String> keepUnmerged) {
      Map<String, Task> map = new HashMap<>();
      Iterator<Task> it = tasks.iterator();
      while (it.hasNext()) {
        Task n = it.next();
        if (keepUnmerged.contains(n.name)) {
          continue;
        }
        Task prev = map.get(n.name);
        if (prev == null) {
          map.put(n.name, n);
        } else {
          it.remove();
          prev.mergeWith(n);
        }
      }
      for (Task t : tasks) {
        t.merge(keepUnmerged);
      }
    }

    private void mergeWith(Task n) {
      executionTime += n.executionTime;
      invocationCount += n.invocationCount;
      tasks.addAll(n.tasks);
    }

    public void toString(final long cutOffTimeMillis, StringBuilder sb, int indent) {
      if (name != null) {
        if (cutOffTimeMillis > 0 && executionTime / 1000000 < cutOffTimeMillis) {
          return;
        }
        for (int i = 0; i < indent; i++) {
          sb.append("  ");
        }
        sb.append(name);
        if (invocationCount > 1) {
          sb.append('(');
          sb.append(invocationCount);
          sb.append(')');
        }
        sb.append(": ");
        sb.append(executionTime / 100000 / 10.);
        sb.append(" ms");
        // time spent in sub-tasks
        if (!tasks.isEmpty()) {
          long correction = 0;
          for (Task subt : tasks) {
            correction += subt.executionTime;
          }
          long taskOwnTime = executionTime - correction;
          if (taskOwnTime > executionTime / 10) {
            // spent more than 10% of total task time in activities other than that of nested tasks
            sb.append("  (own: ");
            sb.append(taskOwnTime / 100000 / 10.);
            sb.append(" ms)");
          }
        }
        sb.append('\n');
      }
      tasks.sort(Comparator.<Task>comparingLong(o -> o.executionTime).reversed());
      for (Task t : tasks) {
        t.toString(cutOffTimeMillis, sb, name == null ? indent : indent + 2);
      }
    }

    @Override
    public String toString() {
      return "PT-Task(" + name + ")";
    }
  }
}
