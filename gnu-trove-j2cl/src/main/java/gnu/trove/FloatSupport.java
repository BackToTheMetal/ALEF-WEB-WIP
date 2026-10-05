package gnu.trove;

/** Float32 normalization using operations emulated by stock J2CL. */
final class FloatSupport {
  private FloatSupport() {}
  static float round(float value) {
    return Float.intBitsToFloat(Float.floatToIntBits(value));
  }
}
