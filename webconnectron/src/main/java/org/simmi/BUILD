load("@io_bazel_rules_closure//closure:defs.bzl", "closure_js_binary")

package(
    licenses = ["notice"],  # Apache 2.0
)

load("@com_google_j2cl//build_defs:rules.bzl", "j2cl_application")

closure_js_binary(
    name = "connectron",
    srcs = ["main.js"],
)

# This is the bazel target that compiles and optimizes whole JavaScript / J2CL app.
# It provides couple of convenient targets:
#  :helloworld produces the helloworld.js that could be used for production.
#  :helloworld_dev produces the helloworld_dev.js that could be used for development.
#  :helloworld_dev_server serves helloworld_dev.js with couple of extras which you can
#    use as a simple development server.
#
# Give it a try:
#   $ bazel run third_party/java_src/j2cl/samples/helloworld/java/com/google/j2cl/samples/helloworld:helloworld_dev_server
#
# If you like to just save a file and see the results, you can just replace bazel w/ ibazel:
#   $ ibazel run third_party/java_src/j2cl/samples/helloworld/java/com/google/j2cl/samples/helloworld:helloworld_dev_server
#
# See the installation instructions for ibazel here:
# https://github.com/bazelbuild/bazel-watcher#installation
j2cl_application(
    name = "connectron",
    entry_points = ["j2cl.connectron.app"],
    deps = [":connectron"],
)
