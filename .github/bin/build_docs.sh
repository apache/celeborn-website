#!/usr/bin/env bash
#
# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

set -o pipefail
set -e
set -x

function exit_with_usage {
  echo "./github/bin/build_docs.sh <ref_version_name> <doc_link_path>"
  exit 1
}

if [ $# -ne 2 ]; then
  exit_with_usage
fi

REF_VERSION_NAME="$1"
DOC_LINK_PATH="$2"

TAR_NAME=${REF_VERSION_NAME##*/}

if [[ "$REF_VERSION_NAME" == *"-tm"* ]] ;then
    DIR_VERSION_NAME=$(echo "$TAR_NAME" | sed -r "s/v*(.*)\.tar\.gz/\1/g")
    if [[ "$REF_VERSION_NAME" == *"-incubating"* ]] ;then
      TAR_DIR_NAME=incubator-celeborn-$DOC_LINK_PATH
    else
      TAR_DIR_NAME=celeborn-$DOC_LINK_PATH
    fi
    wget "https://github.com/apache/celeborn/releases/download/${REF_VERSION_NAME}"
  else
    DIR_VERSION_NAME=$(echo "$TAR_NAME" | sed -r "s/v*(.*)\.tar\.gz/\1/g")
    TAR_DIR_NAME=celeborn-$DIR_VERSION_NAME
    wget "https://github.com/apache/celeborn/archive/refs/${REF_VERSION_NAME}"
fi

tar -xzf $TAR_NAME
cd $TAR_DIR_NAME
mkdocs build
cd ..
mkdir -p docs
if [ -d docs/$DOC_LINK_PATH ]; then rm -r docs/$DOC_LINK_PATH; fi
mv $TAR_DIR_NAME/site docs/$DOC_LINK_PATH
git add .
git commit -m "docs/$DOC_LINK_PATH"
