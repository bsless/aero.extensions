(ns bsless.aero.extensions
  (:require
   [aero.alpha.core :refer [eval-tagged-literal expand-case]]
   [aero.core :as aero]))

(defmethod eval-tagged-literal 'ext/mode
  [tl opts env ks]
  (expand-case (:mode opts) tl opts env ks))

(deftype Secret [obj]
  clojure.lang.IDeref
  (deref [_] obj)
  Object
  (toString [_] "<Secret ...>"))

(defmethod print-method Secret [x w]
  (print-method (str x) w))

(defmethod print-dup Secret [x w]
  (print-dup (str x) w))

(defmethod aero/reader 'ext/secret
  [_opts _tag value]
  (Secret. value))

(defmethod aero/reader 'ext/read-secret
  [{:keys [secret-reader]} _tag value]
  (Secret. (secret-reader value)))

(defmethod aero/reader 'malli/schema
  [{:keys [malli/transformer]} _tag [schema value]]
  ((requiring-resolve 'malli.core/coerce) schema value transformer))
