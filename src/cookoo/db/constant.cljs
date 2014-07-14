(ns cookoo.db.constant)

(defn find-id [mapping id]
  (get-in mapping [:by-id id]))

(defn find-constant [mapping constant]
  (get-in mapping [:by-constant constant]))

(defn relate [mapping id constant]
  (-> mapping
      (assoc-in [:by-id id] constant)
      (assoc-in [:by-constant constant] id)))


