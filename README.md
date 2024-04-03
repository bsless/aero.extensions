[![Clojars Project](https://img.shields.io/clojars/v/io.github.bsless/aero.extensions.svg)](https://clojars.org/io.github.bsless/aero.extensions)
[![Clojars Project](https://img.shields.io/clojars/v/io.github.bsless/aero.extensions.svg?include_prereleases)](https://clojars.org/io.github.bsless/aero.extensions)
[![cljdoc](https://cljdoc.org/badge/io.github.bsless/aero.extensions)](https://cljdoc.org/d/io.github.bsless/aero.extensions)
![Test And Snapshot](https://github.com/bsless/aero.extensions/actions/workflows/test-and-snapshot.yml/badge.svg)

# bsless/aero.extensions

Aero is awesome

Still, it lacks about three features which would make it **shine**:
Another profile dimension, secrets, and secret readers

## Usage

Use aero normally and also load this extension library

```clojure
(require '[aero.core :as aero] '[bsless.aero.extensions])
```

### New tags

#### `ext/mode`

Works exactly like profile, for when you need it

#### `ext/secret`

Wraps the content in an opaque `deref`-able object.
This way printing the loaded configuration won't leak a secret

#### `ext/read-secret`

Having secrets in a config file is convenient, but how can we get them there?

```clojure
#ext/read-secret "path/to/secret"
;; or
#ext/read-secret [:just/whatever "path/to/secret"]
```

Pass a `:secret-reader` to `aero.core/read-config` and let it resolve
the secret according to the specified value at resolution time.

Example - vault client, reader for encryped files

#### `malli/schema`

When some value in your configuration has to conform to a schema

```clojure
#malli/schema [:int 1] ;; pass
#malli/schema [:int "1"] ;; fail
#malli/schema [[:int {:min 2}] 1] ;; fail
```

Use a vector of `[schema value]`.

Optionally pass a `:malli/transformer` when reading the configuration to coerce values.
The second example would pass with

```clojure
(read-config "config.edn" {:malli/transformer mt/string-transformer})
```

Important note: malli isn't included on the class path. Bring it in as an extra dependency.

### Example

Given the config

```clojure
#ext/mode {:fizz #ext/read-secret #profile {:bar "foo/bar" :baz "foo/baz"}
           :buzz #profile {:bar 1 :baz 2}}
```

It will resolve to:

```clojure
(read-config config {:profile :bar :mode :fizz :secret-reader {"foo/bar" :foo}})
;; =>
(Secret. :foo)
```

Keep in mind the secret will print as `<Secret ...>`

## License

Copyright © 2024 Ben Sless

Distributed under the Eclipse Public License version 1.0.
