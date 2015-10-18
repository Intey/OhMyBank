#!/bin/sh

jade $(find src/html -iname "*.jade") --out "resources/public"
