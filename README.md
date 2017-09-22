# mapx

[![License](https://img.shields.io/github/license/chrisjd-uk/mapx.svg)](LICENSE)
[![Clojars Project](https://img.shields.io/clojars/v/chrisjd/mapx.svg)](https://clojars.org/chrisjd/mapx)
[![Build Status](https://travis-ci.org/chrisjd-uk/mapx.svg?branch=master)](https://travis-ci.org/chrisjd-uk/mapx)

A very simple library for performing powerful map transformations in
Clojure.

The problem that this library solves is needing to transform maps from
one domain (e.g. results fetched from a database) into another domain
(e.g. your application's data model).

This process generally requires 3 steps:

- Select, delete or insert certain keys from the input map.
- Apply transformations to values in the map.
- Rename keys.

mapx provides just one core function to achieve this in a declarative,
clear manner: `mapx.core/transform`.

## Installation

Add the following to your `project.clj`:

```
[chrisjd/mapx "0.2.0"]
```


## Documentation

- [API Docs](https://chrisjd-uk.github.io/mapx/)


## Usage

For the examples below:

```clojure
user> (require '[mapx.core :as mx])
nil
```

Inserting missing keys:

``` clojure
user> (mx/transform {:a 1 :b 2 :c 3} :or {:a 123 :d 4})
{:a 1, :b 2, :c 3, :d 4}
```

Selecting keys:

```clojure
user> (mx/transform {:a 1 :b 2 :c 3} :select [:a :c])
{:a 1, :c 3}
```

Deleting keys:

```clojure
user> (mx/transform {:a 1 :b 2 :c 3} :delete [:a])
{:b 2, :c 3}
```

Updating values:

```clojure
user> (mx/transform {:a 1 :b 2 :c 3} :update {:a inc, :c (partial * 20)})
{:a 2, :b 2, :c 60}
```

Renaming keys:

```clojure
user> (mx/transform {:a 1 :b 2 :c 3} :rename {:a :x, :b :y, :c :z})
{:x 1, :y 2, :z 3}
```

All together:

```clojure
user> (mx/transform {:a 1 :b 2 :c 3}
                    :or {:a 123, :d 456}
                    :select [:a :c :d]
                    :delete [:c]
                    :update {:a str}
                    :rename {:a :foo})
{:foo "1", :d 456}
```

Projection:

``` clojure
user> (mx/transform {:a 1 :b 2 :c 3}
                    :project {:a :x :c :z})
{:x 1, :z 3}
```

Update and project:

``` clojure
user> (mx/transform {:a 1 :b 2 :c 3}
                    :update {:a inc, :c dec}
                    :project {:a :x :c :z})
{:x 2, :z 2}
```


## Typical Example #1

A typical example of how mapx can be used in real projects:

``` clojure
(defn make-account
  "Create a new account."
  [& {:as opts}]
  {:post [(s/valid? :data/account %)]}
  (transform opts
             :or {:role :user}
             :project {:email        :data.account/email
                       :password     :data.account/password
                       :role         :data.account/role
                       :user         :data.account/user
                       :subscription :data.account/subscription}))

```

Here, we provide a default value for `:role`, but expect the other
fields to be specified.  The post-condition on the function uses
[spec](https://clojure.org/guides/spec/) to ensure that what we
produce is a well-formed account.


## Typical Example #2

Using with [speconv](https://github.com/chrisjd-uk/speconv) to
succinctly define conversion functions to map the same data between
two distinct (and spec-validated) contexts:

``` clojure
(conversion :db/subscription :data/subscription
            [in]
            (transform in
                       :project {:subscription/expires       :data.subscription/expires
                                 :subscription/period-charge :data.subscription/period-charge
                                 :subscription/period-length :data.subscription/period-length
                                 :db/id                      :data/entity-id}))

(conversion :data/subscription :db/subscription
            [in]
            (transform in
                       :project {:data.subscription/expires       :subscription/expires
                                 :data.subscription/period-charge :subscription/period-charge
                                 :data.subscription/period-length :subscription/period-length
                                 :data/entity-id                  :db/id}))
```

The benefit being that we *explicitly* state which data is to be
carried across domains without the risk of leaking sensitive or
internal information accidentally.


## License

Copyright © 2017 Chris J-D

Distributed under the Eclipse Public License either version 1.0 or (at
your option) any later version.
