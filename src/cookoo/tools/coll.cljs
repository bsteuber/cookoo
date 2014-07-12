(ns cookoo.tools.coll)

(def conjv (comp vec conj))

(defn conjs [coll x]
  (if (nil? coll)
    #{x}
    (conj coll x)))

(defn as-seq [val]
  (if (sequential? val)
    val
    (list val)))
