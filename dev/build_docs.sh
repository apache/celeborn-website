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

REF_VERSION_NAME="$1"
TAR_NAME=${REF_VERSION_NAME##*/}
DIR_VERSION_NAME=$(echo "$TAR_NAME" | sed -r "s/v*(.*)\.tar\.gz/\1/g")
TAR_DIR_NAME=incubator-celeborn-$DIR_VERSION_NAME
if [[ $DIR_VERSION_NAME == "main" ]]; then
    DOC_VERSION="latest"
else
    DOC_VERSION=$DIR_VERSION_NAME
fi

wget "https://github.com/apache/incubator-celeborn/archive/refs/${REF_VERSION_NAME}"
tar -xzf $TAR_NAME
cd $TAR_DIR_NAME
mkdocs build
cd ..
mkdir -p docs
if [ -d docs/$DOC_VERSION ]; then rm -r docs/$DOC_VERSION; fi
mv $TAR_DIR_NAME/site docs/$DOC_VERSION
git add .
git commit -m "docs/$DOC_VERSION"