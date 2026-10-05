param(
    [string]$ApiInventoryPath = "compatibility/openjdk-awt-api.json",
    [string]$ImplementationStatusPath = "compatibility/implementation-status.json",
    [string]$OutputPath = "compatibility/compatibility-manifest.json"
)

$ErrorActionPreference = "Stop"
$apiInventory = Get-Content -LiteralPath $ApiInventoryPath -Raw | ConvertFrom-Json
$implementationStatus = Get-Content -LiteralPath $ImplementationStatusPath -Raw | ConvertFrom-Json
$excludedTypes = @("java.awt.FileDialog", "java.awt.JobAttributes", "java.awt.PageAttributes", "java.awt.PrintGraphics", "java.awt.PrintJob")
$unavailableTypes = @("java.awt.AWTPermission", "java.awt.event.AWTEventListenerProxy", "java.awt.MouseInfo", "java.awt.PointerInfo", "java.awt.Robot", "java.awt.SecondaryLoop", "java.awt.SplashScreen", "java.awt.SystemTray", "java.awt.Taskbar", "java.awt.TrayIcon")
$webMappedTypes = @("java.awt.Button", "java.awt.Canvas", "java.awt.Checkbox", "java.awt.CheckboxGroup", "java.awt.CheckboxMenuItem", "java.awt.Choice", "java.awt.Component", "java.awt.Container", "java.awt.Dialog", "java.awt.Frame", "java.awt.Label", "java.awt.List", "java.awt.Menu", "java.awt.MenuBar", "java.awt.MenuComponent", "java.awt.MenuItem", "java.awt.Panel", "java.awt.PopupMenu", "java.awt.ScrollPane", "java.awt.Scrollbar", "java.awt.TextArea", "java.awt.TextComponent", "java.awt.TextField", "java.awt.Window")
$implementedTypes = @($implementationStatus.implementedTypes)
$partialImplementedTypes = @($implementationStatus.partialTypes)
$newExactTypes = @("java.awt.ActiveEvent", "java.awt.Adjustable", "java.awt.AWTError", "java.awt.AWTEvent", "java.awt.AWTException", "java.awt.DisplayMode", "java.awt.Event", "java.awt.FontFormatException", "java.awt.HeadlessException", "java.awt.IllegalComponentStateException", "java.awt.ItemSelectable", "java.awt.MenuShortcut", "java.awt.Polygon", "java.awt.RenderingHints", "java.awt.Stroke", "java.awt.Transparency", "java.awt.event.ActionEvent", "java.awt.event.ActionListener", "java.awt.event.AdjustmentEvent", "java.awt.event.AdjustmentListener", "java.awt.event.ItemEvent", "java.awt.event.ItemListener", "java.awt.event.TextEvent", "java.awt.event.TextListener", "java.awt.geom.GeneralPath", "java.awt.geom.FlatteningPathIterator", "java.awt.image.DataBuffer", "java.awt.image.DataBufferByte", "java.awt.image.DataBufferDouble", "java.awt.image.DataBufferFloat", "java.awt.image.DataBufferInt", "java.awt.image.DataBufferShort", "java.awt.image.DataBufferUShort", "java.awt.image.RasterFormatException")
$newLimitedTypes = @("java.awt.BasicStroke", "java.awt.BufferCapabilities", "java.awt.ComponentOrientation", "java.awt.Cursor", "java.awt.GridBagConstraints", "java.awt.ImageCapabilities", "java.awt.event.InvocationEvent", "java.awt.geom.Arc2D", "java.awt.geom.Area", "java.awt.geom.Ellipse2D", "java.awt.geom.RoundRectangle2D", "java.awt.image.BandedSampleModel", "java.awt.image.ComponentSampleModel", "java.awt.image.MultiPixelPackedSampleModel", "java.awt.image.PixelInterleavedSampleModel", "java.awt.image.Raster", "java.awt.image.SampleModel", "java.awt.image.SinglePixelPackedSampleModel", "java.awt.image.WritableRaster")
$plannedExactTypes = @("java.awt.AWTKeyStroke", "java.awt.event.AWTEventListener", "java.awt.event.HierarchyBoundsAdapter", "java.awt.event.HierarchyBoundsListener", "java.awt.event.HierarchyEvent", "java.awt.event.HierarchyListener", "java.awt.event.InputMethodEvent", "java.awt.event.InputMethodListener", "java.awt.event.PaintEvent", "java.awt.event.WindowAdapter", "java.awt.event.WindowEvent", "java.awt.event.WindowFocusListener", "java.awt.event.WindowListener", "java.awt.event.WindowStateListener", "java.awt.GridBagLayout", "java.awt.GridBagLayoutInfo")
$plannedWebMappedTypes = @("java.awt.ContainerOrderFocusTraversalPolicy", "java.awt.DefaultFocusTraversalPolicy", "java.awt.DefaultKeyboardFocusManager", "java.awt.FocusTraversalPolicy", "java.awt.GraphicsConfigTemplate", "java.awt.GraphicsConfiguration", "java.awt.GraphicsDevice", "java.awt.GraphicsEnvironment", "java.awt.KeyboardFocusManager", "java.awt.KeyEventDispatcher", "java.awt.KeyEventPostProcessor", "java.awt.MediaTracker", "java.awt.ScrollPaneAdjustable", "java.awt.SystemColor", "java.awt.Toolkit")
$plannedLimitedTypes = @("java.awt.AWTEventMulticaster", "java.awt.TexturePaint")
$missingJ2clSignatureTypes = @("java.io.File", "java.io.FilenameFilter", "java.lang.Character`$Subset", "java.lang.reflect.InvocationTargetException", "java.security.BasicPermission", "java.util.EventListenerProxy", "java.util.Hashtable", "java.util.Properties", "java.util.ResourceBundle")
$outOfScopeSignatureTypes = @("javax.swing.JFrame", "javax.swing.JMenuBar")
function Get-MissingJ2clSignatureDependencies {
    param([string]$Signature)
    return @($missingJ2clSignatureTypes | Where-Object { $Signature -match "(?<![A-Za-z0-9_`$])$([regex]::Escape($_))(?![A-Za-z0-9_`$])" })
}
function Get-OutOfScopeSignatureDependencies {
    param([string]$Signature)
    return @($outOfScopeSignatureTypes | Where-Object { $Signature -match "(?<![A-Za-z0-9_`$])$([regex]::Escape($_))(?![A-Za-z0-9_`$])" })
}
$evidenceTypes = @($implementedTypes + $partialImplementedTypes)
$duplicateEvidenceTypes = @($evidenceTypes | Group-Object | Where-Object Count -gt 1)
if ($duplicateEvidenceTypes.Count -gt 0) {
    throw "Implementation status contains duplicate or conflicting types: $($duplicateEvidenceTypes.Name -join ', ')"
}
$unknownEvidenceTypes = @($evidenceTypes | Where-Object { $_ -notin $apiInventory.types.name })
if ($unknownEvidenceTypes.Count -gt 0) {
    throw "Implementation status contains types absent from the reference inventory: $($unknownEvidenceTypes -join ', ')"
}
$entries = foreach ($type in $apiInventory.types) {
    $status = "deferred"
    $reason = "Requires per-type compatibility analysis"
    $typeMissingJ2clDependencies = Get-MissingJ2clSignatureDependencies $type.declaration
    if ($type.name.StartsWith("java.awt.print.") -or $excludedTypes -contains $type.name) {
        $status = "excluded"
        $reason = "File dialogs and printing are outside the approved project scope"
    } elseif ($typeMissingJ2clDependencies.Count -gt 0) {
        $status = "unavailable"
        $reason = "The type declaration requires missing J2CL-owned type(s): $($typeMissingJ2clDependencies -join ', ')"
    } elseif ($unavailableTypes -contains $type.name) {
        $status = "unavailable"
        $reason = "No safe or meaningful browser equivalent"
    } elseif ($webMappedTypes -contains $type.name) {
        $status = "web-mapped"
        $reason = if ($evidenceTypes -contains $type.name) { "Implemented through a semantic DOM or Canvas peer with documented browser limits" } else { "Planned semantic DOM or Canvas peer implementation" }
    } elseif ($partialImplementedTypes -contains $type.name) {
        $status = "limited"
        $reason = "Partially implemented in the stock-J2CL profile with documented API or browser-semantic limits"
    } elseif ($type.name -in @("java.awt.Desktop", "java.awt.EventQueue")) {
        $status = "limited"
        $reason = "Only non-blocking browser-compatible operations can be mapped"
    } elseif ($plannedExactTypes -contains $type.name) {
        $status = "exact"
        $reason = "Planned pure Java contract with differential tests"
    } elseif ($plannedWebMappedTypes -contains $type.name) {
        $status = "web-mapped"
        $reason = "Planned browser mapping with portable model and browser-contract tests"
    } elseif ($plannedLimitedTypes -contains $type.name) {
        $status = "limited"
        $reason = "Planned implementation with documented browser rendering limits"
    } elseif ($newExactTypes -contains $type.name -or $type.name -in @("java.awt.Dimension", "java.awt.Insets", "java.awt.Point", "java.awt.Rectangle", "java.awt.Shape", "java.awt.geom.IllegalPathStateException", "java.awt.geom.NoninvertibleTransformException", "java.awt.geom.PathIterator", "java.awt.geom.Rectangle2D")) {
        $status = "exact"
        $reason = "Pure Java implementation"
    } elseif ($newLimitedTypes -contains $type.name -or $type.name -in @("java.awt.geom.CubicCurve2D", "java.awt.geom.Dimension2D", "java.awt.geom.Line2D", "java.awt.geom.Path2D", "java.awt.geom.Point2D", "java.awt.geom.QuadCurve2D", "java.awt.geom.RectangularShape")) {
        $status = "limited"
        $reason = "Pure Java implementation with documented cloning or curved-boundary hit-testing limits"
    } elseif ($type.name -eq "java.awt.geom.AffineTransform") {
        $status = "limited"
        $reason = "Core matrix behavior is implemented; transformed-shape region queries have documented limits"
    } elseif ($type.package -eq "java.awt.event" -and $implementedTypes -contains $type.name) {
        $status = "exact"
        $reason = "Portable Java event model with deterministic browser-neutral metadata"
    } elseif ($type.name -in @("java.awt.BorderLayout", "java.awt.CardLayout", "java.awt.FlowLayout", "java.awt.GridLayout", "java.awt.LayoutManager", "java.awt.LayoutManager2")) {
        $status = "exact"
        $reason = "Pure Java layout implementation with differential bounds tests"
    } elseif ($implementedTypes -contains $type.name) {
        $status = "limited"
        $reason = "Implemented in the stock-J2CL profile with documented API or browser-semantic limits"
    } elseif ($type.package -eq "java.awt.geom") {
        $status = "exact"
        $reason = "Planned pure Java implementation with differential tests"
    } elseif ($type.package -in @("java.awt.color", "java.awt.dnd", "java.awt.font", "java.awt.im", "java.awt.im.spi", "java.awt.image", "java.awt.image.renderable", "java.awt.desktop")) {
        $status = "limited"
        $reason = "Browser or stock-J2CL constraints require a reduced contract"
    }
    if ($status -eq "deferred") {
        throw "Type has no explicit support disposition: $($type.name)"
    }
    $members = foreach ($member in $type.members) {
        $memberStatus = $status
        $memberReason = $reason
        if ($type.name -in @("java.awt.geom.CubicCurve2D", "java.awt.geom.Dimension2D", "java.awt.geom.Line2D", "java.awt.geom.Path2D", "java.awt.geom.Point2D", "java.awt.geom.QuadCurve2D", "java.awt.geom.RectangularShape")) {
            $memberStatus = "exact"
            $memberReason = "Pure Java implementation"
        }
        if ($type.name -eq "java.awt.geom.AffineTransform") {
            $memberStatus = "exact"
            $memberReason = "Pure Java matrix implementation"
        }
        if ($newLimitedTypes -contains $type.name) {
            $memberStatus = "exact"
            $memberReason = "Pure Java or browser-profile implementation"
        }
        if ($type.name -eq "java.awt.AWTEventMulticaster") {
            $memberStatus = "exact"
            $memberReason = "Listener composition and dispatch follow the OpenJDK contract"
        }
        if ($type.name -eq "java.awt.Toolkit" -and $member -match '\bgetPrintJob\(') {
            $memberStatus = "excluded"
            $memberReason = "Printing is outside the approved project scope"
        } elseif ($type.name -eq "java.awt.EventQueue" -and $member -match '\b(invokeAndWait|createSecondaryLoop)\(') {
            $memberStatus = "unavailable"
            $memberReason = "Blocking or nested event loops cannot be preserved on the browser main thread"
        } elseif ($type.name -eq "java.awt.EventQueue" -and $member -match '\binvokeLater\(') {
            $memberStatus = "web-mapped"
            $memberReason = "Maps to browser task scheduling"
        } elseif ($type.name -eq "java.awt.EventQueue" -and $member -match '\b(getNextEvent|postEvent|peekEvent|push)\(') {
            $memberStatus = "limited"
            $memberReason = "Uses an asynchronous browser-main-thread queue; getNextEvent reports an empty queue instead of blocking and pushed queues cannot be popped through inaccessible protected APIs"
        } elseif ($type.name -eq "java.awt.Toolkit" -and $member -match '\bgetSystemClipboard\(') {
            $memberStatus = "limited"
            $memberReason = "Provides a synchronous application-local clipboard because the browser system clipboard API is asynchronous and permission-gated"
        } elseif ($type.name -eq "java.awt.Toolkit" -and $member -match '\bgetSystemSelection\(') {
            $memberStatus = "limited"
            $memberReason = "Returns null because browsers do not expose the platform selection clipboard"
        } elseif ($type.name -eq "java.awt.Toolkit" -and $member -match '\bcreateDragGestureRecognizer\(') {
            $memberStatus = "web-mapped"
            $memberReason = "Creates the supported browser mouse drag gesture recognizer and returns null for unsupported recognizer classes"
        } elseif ($type.name -eq "java.awt.Window" -and $member -match '\bapplyResourceBundle\(java.lang.String') {
            $memberStatus = "limited"
            $memberReason = "Applies the default locale orientation without loading an unavailable java.util.ResourceBundle"
        } elseif ($type.name -eq "java.awt.image.ColorConvertOp" -and $member -match '\b(ColorConvertOp|getICC_Profiles|filter|createCompatibleDestImage|createCompatibleDestRaster)\(') {
            $memberStatus = "limited"
            $memberReason = "Standard J2CL color spaces are converted in pure Java; arbitrary device ICC transforms require an unavailable native color-management engine"
        } elseif ($type.name -eq "java.awt.Desktop" -and $member -match '\b(open|edit|print|moveToTrash)\(') {
            $memberStatus = "unavailable"
            $memberReason = "Requires unrestricted local desktop or file access"
        } elseif ($type.name -eq "java.awt.Desktop" -and $member -match '\b(setAboutHandler|setPreferencesHandler|setOpenFileHandler|setOpenURIHandler|setQuitStrategy|openHelpViewer)\(') {
            $memberStatus = "unavailable"
            $memberReason = "The browser cannot expose the corresponding operating-system application integration contract"
        } elseif ($type.name -eq "java.awt.Desktop" -and $member -match '\bsetPrintFileHandler\(') {
            $memberStatus = "excluded"
            $memberReason = "Printing is outside the approved project scope"
        } elseif ($type.name -eq "java.awt.Desktop" -and $member -match '\b(addAppEventListener|removeAppEventListener)\(') {
            $memberStatus = "limited"
            $memberReason = "Page visibility maps foreground and hidden events; operating-system reopen, sleep, and user-session events are not generated"
        } elseif ($type.name -eq "java.awt.color.ICC_Profile" -and $member -match '\b(getInstance|write)\(java.lang.String') {
            $memberStatus = "unavailable"
            $memberReason = "Synchronous filesystem profile access is unavailable in the browser profile"
        } elseif ($type.name -eq "java.awt.color.ICC_Profile" -and $member -match '\bsetData\(') {
            $memberStatus = "limited"
            $memberReason = "Profile tags are validated and serialized, without a native color-management engine validating device-specific transforms"
        } elseif ($type.name -eq "java.awt.color.ICC_ColorSpace" -and $member -match '\b(toRGB|fromRGB|toCIEXYZ|fromCIEXYZ)\(') {
            $memberStatus = "limited"
            $memberReason = "Standard RGB, gray, XYZ, and PYCC spaces use deterministic pure-Java transforms; custom device profiles require an unavailable color-management engine"
        } elseif ($type.name -in @("java.awt.geom.CubicCurve2D", "java.awt.geom.Dimension2D", "java.awt.geom.Line2D", "java.awt.geom.Point2D", "java.awt.geom.QuadCurve2D", "java.awt.geom.RectangularShape") -and $member -match '\bclone\(') {
            $memberStatus = "limited"
            $memberReason = "Preserves the built-in WebAWT concrete types but cannot generically clone an unknown subclass because stock J2CL omits Object.clone"
        } elseif ($type.name -in @("java.awt.geom.CubicCurve2D", "java.awt.geom.Path2D", "java.awt.geom.QuadCurve2D") -and $member -match '\b(contains|intersects)\(') {
            $memberStatus = "limited"
            $memberReason = "Curved boundaries are classified through adaptive flattening; results can differ within the documented tolerance or for pathological coordinates"
        } elseif ($type.name -eq "java.awt.geom.CubicCurve2D" -and $member -match '\bsolveCubic\(') {
            $memberStatus = "limited"
            $memberReason = "Analytic real-root solving is implemented; extreme ill-conditioned coefficients can differ from OpenJDK refinement behavior"
        } elseif ($type.name -eq "java.awt.BasicStroke" -and $member -match '\bcreateStrokedShape\(') {
            $memberStatus = "limited"
            $memberReason = "Dependency-free path expansion is functional; high-complexity joins and pathological paths can differ from OpenJDK"
        } elseif ($type.name -in @("java.awt.BufferCapabilities", "java.awt.ImageCapabilities", "java.awt.geom.Arc2D", "java.awt.geom.Ellipse2D", "java.awt.geom.RoundRectangle2D") -and $member -match '\bclone\(') {
            $memberStatus = "limited"
            $memberReason = "Built-in values are copied, but stock J2CL cannot generically preserve an unknown subclass through Object.clone"
        } elseif ($type.name -eq "java.awt.ComponentOrientation" -and $member -match '\bgetOrientation\(java.util.ResourceBundle\)') {
            $memberStatus = "unavailable"
            $memberReason = "Stock J2CL does not provide java.util.ResourceBundle; this overload is omitted"
        } elseif ($type.name -in @("java.awt.image.BufferedImage", "java.awt.image.ImageConsumer", "java.awt.image.ImageFilter", "java.awt.image.MemoryImageSource") -and $member -match 'java.util.Hashtable') {
            $memberStatus = "unavailable"
            $memberReason = "Stock J2CL does not provide java.util.Hashtable; this signature cannot be emitted"
        } elseif ($type.name -eq "java.awt.Cursor" -and $member -match '\bgetSystemCustomCursor\(') {
            $memberStatus = "limited"
            $memberReason = "No desktop cursor registry exists in a browser; predefined and CSS-mapped cursors are supported"
        } elseif ($type.name -eq "java.awt.event.InvocationEvent" -and $member -match 'InvocationEvent\(java.lang.Object, java.lang.Runnable, java.lang.Object, boolean\)|\bdispatch\(') {
            $memberStatus = "limited"
            $memberReason = "Runnable dispatch and exception capture work; notifier wake-up semantics are unavailable without JVM threads"
        } elseif ($type.name -eq "java.awt.AWTEventMulticaster" -and $member -match '\bgetListeners\(') {
            $memberStatus = "limited"
            $memberReason = "Supports EventListener and every AWT listener interface handled by the multicaster; arbitrary custom subtypes cannot be tested because stock J2CL omits Class.isInstance and reflective array construction"
        } elseif ($type.name -eq "java.awt.MediaTracker" -and $member -match '\bwaitFor(All|ID)\(') {
            $memberStatus = "limited"
            $memberReason = "Starts image production and reports current completion without blocking the browser main thread"
        } elseif ($type.name -eq "java.awt.Font" -and $member -match '\b(createFont|createFonts)\([^)]*java.io.InputStream') {
            $memberStatus = "unavailable"
            $memberReason = "Synchronous font parsing and registration cannot be implemented through the asynchronous browser FontFace API without embedding a font parser"
        } elseif ($type.name -eq "java.awt.geom.Area" -and $member -match '\b(add|subtract|intersect|exclusiveOr|equals|isSingular|contains|intersects)\(') {
            $memberStatus = "limited"
            $memberReason = "Vector slab operations are exact for ordinary linear paths; curved input is flattened and pathological topology has documented tolerances"
        } elseif ($type.name -eq "java.awt.geom.AffineTransform" -and $member -match '\bcreateTransformedShape\(') {
            $memberStatus = "limited"
            $memberReason = "Returns a transformed Path2D; curved-boundary hit testing inherits the adaptive-flattening tolerance"
        }
        $memberImplementationState = if ($partialImplementedTypes -contains $type.name) { "partial" } elseif ($implementedTypes -contains $type.name) { "implemented" } else { "not-started" }
        if ($type.name -eq "java.awt.ComponentOrientation" -and $member -match '\bgetOrientation\(java.util.ResourceBundle\)') {
            $memberImplementationState = "not-started"
        } elseif ($type.name -eq "java.awt.Toolkit" -and $member -match '\bgetPrintJob\(') {
            $memberImplementationState = "not-started"
        } elseif ($type.name -eq "java.awt.Font" -and $member -match '\b(createFont|createFonts)\(') {
            $memberImplementationState = "not-started"
        } elseif ($type.name -eq "java.awt.image.BufferedImage" -and $member -match 'java.util.Hashtable|\b(getSources|addTileObserver|removeTileObserver)\(') {
            $memberImplementationState = "not-started"
        } elseif ($type.name -in @("java.awt.image.ImageConsumer", "java.awt.image.ImageFilter", "java.awt.image.MemoryImageSource") -and $member -match 'java.util.Hashtable') {
            $memberImplementationState = "not-started"
        } elseif ($type.name -eq "java.awt.image.IndexColorModel" -and $member -match '\bconvertToIntDiscrete\(') {
            $memberImplementationState = "not-started"
        } elseif ($type.name -eq "java.awt.Desktop" -and $member -match '\b(setAboutHandler|setPreferencesHandler|setOpenFileHandler|setPrintFileHandler|setOpenURIHandler|setQuitStrategy|openHelpViewer)\(') {
            $memberImplementationState = "not-started"
        } elseif ($type.name -eq "java.awt.color.ICC_Profile" -and $member -match '\b(getInstance|write)\(java.lang.String') {
            $memberImplementationState = "not-started"
        }
        $memberMissingJ2clDependencies = Get-MissingJ2clSignatureDependencies $member
        $memberOutOfScopeDependencies = Get-OutOfScopeSignatureDependencies $member
        if ($memberStatus -notin @("excluded", "unavailable") -and $memberMissingJ2clDependencies.Count -gt 0) {
            $memberStatus = "unavailable"
            $memberImplementationState = "not-started"
            $memberReason = "The signature requires missing J2CL-owned type(s): $($memberMissingJ2clDependencies -join ', ')"
        } elseif ($memberStatus -notin @("excluded", "unavailable") -and $memberOutOfScopeDependencies.Count -gt 0) {
            $memberStatus = "unavailable"
            $memberImplementationState = "not-started"
            $memberReason = "The signature requires out-of-scope Swing type(s): $($memberOutOfScopeDependencies -join ', ')"
        }
        [ordered]@{
            signature = $member
            supportStatus = $memberStatus
            implementationState = $memberImplementationState
            reason = $memberReason
        }
    }
    [ordered]@{
        type = $type.name
        package = $type.package
        supportStatus = $status
        implementationState = if ($partialImplementedTypes -contains $type.name) { "partial" } elseif ($implementedTypes -contains $type.name) { "implemented" } else { "not-started" }
        reason = $reason
        members = @($members)
    }
}
$manifest = [ordered]@{
    schemaVersion = 1
    generatedAtUtc = [DateTime]::UtcNow.ToString("o")
    referenceInventory = $ApiInventoryPath
    implementationStatus = $ImplementationStatusPath
    statuses = @("exact", "web-mapped", "limited", "deferred", "unavailable", "excluded")
    entries = @($entries)
}
$resolvedOutputPath = Join-Path (Get-Location) $OutputPath
New-Item -ItemType Directory -Force -Path (Split-Path $resolvedOutputPath) | Out-Null
$manifest | ConvertTo-Json -Depth 6 | Set-Content -LiteralPath $resolvedOutputPath -Encoding utf8
Write-Output "Wrote $(@($entries).Count) compatibility entries to $resolvedOutputPath"
