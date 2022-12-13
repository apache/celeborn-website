---
license: |
  Licensed to the Apache Software Foundation (ASF) under one or more
  contributor license agreements.  See the NOTICE file distributed with
  this work for additional information regarding copyright ownership.
  The ASF licenses this file to You under the Apache License, Version 2.0
  (the "License"); you may not use this file except in compliance with
  the License.  You may obtain a copy of the License at
      http://www.apache.org/licenses/LICENSE-2.0
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.
---

Building Docs and Website
===

## Setup Python

Follow the [Python official document](https://wiki.python.org/moin/BeginnersGuide) to install Python 3.7 or above.

??? note "(optional) use `pyenv` to manage Python virtualenv on macOS"

    Optionally, [pyenv](https://github.com/pyenv/pyenv) is recommended to manage Python environments on macOS.

    Install from Homebrew

    ```bash
    brew install pyenv pyenv-virtualenv
    ```

    Setup in `~/.zshrc`

    ```bash
    eval "$(pyenv init -)"
    eval "$(pyenv virtualenv-init -)"
    ```

    Install `virtualenv`

    ```bash
    pyenv install 3.9.13
    pyenv virtualenv 3.9.13 celeborn
    ```

    Localize `virtualenv`

    ```bash
    pyenv local celeborn
    ```

## Install dependencies

```bash
pip install -r requirements.txt
```

## Preview website

```
mkdocs serve
```

Open [http://127.0.0.1:8000/](http://127.0.0.1:8000/) in browser.
