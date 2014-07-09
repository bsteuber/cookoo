(ns cookoo.tools.coll)

(defn as-seq [val]
  (if (sequential? val)
    val
    (list val)))
