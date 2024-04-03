(ns bsless.aero.extensions-test
  (:require
   [malli.transform :as mt]
   [matcher-combinators.test :refer [match?]]
   [matcher-combinators.matchers :refer [via]]
   [clojure.test :refer [deftest is]]
   [aero.core :as aero]
   [bsless.aero.extensions]))

(defn read-config
  [^String s opts]
  (aero/read-config (java.io.StringReader. s) opts))

(def config
  "#ext/mode {:fizz #ext/read-secret #profile {:bar \"foo/bar\" :baz \"foo/baz\"}
              :buzz #profile {:bar 1 :baz 2}}")

(deftest extensions-test
  (is (match? (via deref :foo)
              (read-config config {:profile :bar :mode :fizz :secret-reader {"foo/bar" :foo}}))))

(deftest malli-schema-test
  (is (= 1 (read-config "#malli/schema [:int 1]" {})))
  (is (thrown-with-msg? Exception #"malli.core/coercion" (read-config "#malli/schema [:int \"1\"]" {})))
  (is (= 1 (read-config "#malli/schema [:int \"1\"]" {:malli/transformer mt/string-transformer})))
  (is (thrown-with-msg? Exception #"malli.core/coercion" (read-config "#malli/schema [:int \"a\"]" {})))
  (is (= 1 (read-config "#malli/schema [:int #long \"1\"]" {})))
  (is (thrown-with-msg? Exception #"malli.core/coercion" (read-config "#malli/schema [[:int {:min 2}] 1]" {}))))
