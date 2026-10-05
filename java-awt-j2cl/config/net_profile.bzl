"""Select external java.net ownership only when the consumer supplies those classes."""
def _external_net_impl(ctx):
    return []

external_net = rule(implementation = _external_net_impl, build_setting = config.bool(flag = True))
