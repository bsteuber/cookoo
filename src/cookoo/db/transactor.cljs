(ns cookoo.db.transactor
  (:require [cookoo.db.knowledge-base :as kb]
            [cookoo.tools.debug :refer [fail log]]))

(def db (atom {}))

(defn save [] @db)

(defn load! [store]
  (reset! db store))

(defn clear! []
  (swap! db assoc :index {} :facts #{}))

(defn update! [& args]
  (apply swap! db update-in args))

(defn get! [& path]
  (get-in @db path))

(clear!)

(defn query [obj attr]
  (log "(query)" obj attr)
  (log (kb/query (get! :index) obj attr)))

(defn fact? [fact]
  (contains? (get! :facts) fact))

(defn fact! [fact]
  (when-not (= 3 (count fact))
    (fail "illegal fact length" (count fact) fact))
  (update! [:facts] conj fact))

(defn deny! [fact]
  (update! [:facts] disj fact))

(defn index! [facts]
  (update! [:index] kb/facts facts))

(defn index-block [obj attr value]
  (let [index-fns (query attr :index)       
        results (map #(% obj value) index-fns)
        block   (vec (apply concat results))]
    block))

(defn implicit-facts []
  (let [data (get! :data)
        ]
    nil
    )
  )

(defn commit! []
  (index! (get! :facts))
  (index! (implicit-facts)))

