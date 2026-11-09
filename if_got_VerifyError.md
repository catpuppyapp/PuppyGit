if Composable page got `java.lang.VerifyError`:

at first, try: clean gradle cache

then re-build and launch

if still err, then try: move lambdas out from @Composable function

then re-build and launch

if still err, then try: upgrade compose compiler version
