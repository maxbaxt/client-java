#
# Copyright (C) 2020 Grakn Labs
#
# This program is free software: you can redistribute it and/or modify
# it under the terms of the GNU Affero General Public License as
# published by the Free Software Foundation, either version 3 of the
# License, or (at your option) any later version.
#
# This program is distributed in the hope that it will be useful,
# but WITHOUT ANY WARRANTY; without even the implied warranty of
# MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
# GNU Affero General Public License for more details.
#
# You should have received a copy of the GNU Affero General Public License
# along with this program.  If not, see <https://www.gnu.org/licenses/>.
#

package(default_visibility = ["//test/behaviour:__subpackages__"])
load("@graknlabs_dependencies//tool/checkstyle:rules.bzl", "checkstyle_test")
load("@graknlabs_common//test/server:rules.bzl", "grakn_test")

java_library(
    name = "steps",
    srcs = [
        "AttributeTypeSteps.java"
    ],
    deps = [
        # Internal Package Dependencies
        "//:client-java",
        "//test/behaviour/connection:steps",


        # External Maven Dependencies
        "@maven//:junit_junit",
        "@maven//:io_cucumber_cucumber_java",
    ],
    visibility = ["//visibility:public"],
)

grakn_test(
    name = "test",
    srcs = [
        "AttributeTypeTest.java"
    ],
    test_class = "grakn.client.test.behaviour.concept.type.attributetype.AttributeTypeTest",
    deps = [
        # External Maven Dependencies
        "@maven//:io_cucumber_cucumber_junit",
    ],
    runtime_deps = [
        ":steps",
        "//test/behaviour/config:parameters",
        "//test/behaviour/concept/thing/attribute:steps",
        "//test/behaviour/concept/type/thingtype:steps",
        "//test/behaviour/connection/database:steps",
        "//test/behaviour/connection/session:steps",
        "//test/behaviour/connection/transaction:steps",
    ],
    data = [
        "@graknlabs_verification//behaviour/concept/type:attributetype.feature",
    ],
    grakn_artifact = "@graknlabs_grakn_core_artifact//file",
    size = "medium",
)

checkstyle_test(
    name = "checkstyle",
    targets = [
        ":steps",
        ":test",
    ],
)