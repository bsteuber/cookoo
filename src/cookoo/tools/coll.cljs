(ns cookoo.tools.coll)

(def conjv (comp vec conj))
(def conjs (comp #(hash-set &)% conj))

(defn as-seq [val]
  (if (sequential? val)
    val
    (list val)))
