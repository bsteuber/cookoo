(ns cookoo.db.indexers)

(defn inverse [obj val]
  [val obj])

(defn str-prefixes [s]
  (map #(sub s 0 %)
       (range 0 (inc (count s)))))

(defn prefix [obj s]
  [(str-prefixes s) obj])