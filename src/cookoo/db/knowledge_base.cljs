(ns cookoo.db.knowledge-base
  (:require [cookoo.tools.coll :refer [conjv conjs])))

(def kb (atom {}))

(defn load! [load-kb]
  (reset! kb load-kb))

(defn clear! []
  (load! {}))

(defn query-kb [path]
  (get-in @kb path))

(defn query-data [obj attr]
  (query-kb [:data attr obj]))

(defn query-index [obj attr]
  (query-kb [:index attr obj]))

(defn query-log []
  (query-kb [:log]))

(defn update! [& update-args]
  (apply swap! kb update-in  update-args))

(defn update-data! [obj attr & update-args]
  (update! [:data attr obj] update-args))

(defn update-index! [obj attr & update-args]
  (update! [:index attr obj] update-args))

(defn log! [method obj attr value]
  (update! [:log] conjv [method obj attr value]))

(defn query [obj attr]
  (or (query-index obj attr)
      (query-data  obj attr)
      (query attr :default)))

(defn card [attr]
  (query attr :card))

(defn multi? [attr]
  (#{:set :list} (card attr)))

(defn fact? [obj attr value]
  (let [val (query obj attr)]
    (if (multi? attr)    
      (some #{value} val)
      (= value val))))

(defn index-additions [obj attr value]
  (->> (query attr :index)
       (map #(% obj value))
       (apply concat))))

(defn deny-nolog! [obj attr value]
  (update-data! obj attr
     (case (card attr)
       :list #(remove #{value} %)
       :set  #(disj % value)
       (constantly nil)))
  (doseq [[iobj iattr ival] (index-additions obj attr value)]
     (update-index! iobj iattr disj ival)))

(defn deny! [obj attr value]
  (log! :deny obj attr value)
  (deny-nolog! obj attr value))

(defn fact! [obj attr value]
  (log! :fact obj attr value)
  (when-not (multi? attr)
    (deny-nolog! obj attr (query obj attr)))    
  (update-data! obj attr
     (case (card attr)
       :list #(conjv % value)
       :set  #(conjs % value)
       (constantly value)))
  (doseq [[iobj iattr ival] (index-additions obj attr value)]
     (update-index! iobj iattr conjs ival)))
