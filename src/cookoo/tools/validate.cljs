(ns cookoo.tools.validate
  (:require [cookoo.tools.coll :refer [conjv]]
            [cookoo.tools.log :refer [log]]))

(def validate-state (atom {}))

(defn get! [path]
  (get-in @validate-state (vec path)))

(defn change! [& args]
  (apply swap! validate-state update-in args))

(defn put! [path x]
  (change! path (constantly x)))

(defn error! [err]
  (change! [:errors] conjv err))

(defn assert! [res err]    
  (when-not res
    (error! err))
  (boolean res))

(defn validator [pred msg]
  (fn [x]
    (let [path [:cache [x msg]]
    	  entry (get! path)]
      (if (nil? entry)
        (let [res (assert! (pred x) [x msg])]
	  (put! path res))
	entry))))

(defn run [validators x]
  (let [vs (remove nil? (flatten [validators]))
        p  (apply every-pred vs)]
    #_(log vs)
    (p x)))

(defn wrap [validators msg]
  (validator
    (fn [x]
      (run validators x))
    msg))

(defn validate [validators x]
  (reset! validate-state {})
  (run validators x)
  (get! [:errors]))

(defn ok? [x]
  (empty? x))

(defn pr-errors [errors print-fn]
  (if errors
    (do (log "validation errors:")
        (doseq [[x s] errors]
          (log (str "[" (print-fn x) "]") s))
        (log (str "\nTotal Errors: " (count errors))))
    (log "Validation passed :)")))
