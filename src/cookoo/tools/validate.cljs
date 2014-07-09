(ns cookoo.tools.validate
  (:require [cookoo.tools.coll :refer [conjv])))

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
  (doseq [val (flatten [validators])]
    (val x)))

(defn wrap [validators msg]
  (validator
    (fn [x]
      (run validators x))
    msg))

(defn validate [validators x]
  (reset! validate-state {})
  (run validators x)
  (get! [:errors]))

(defn prn! [s]
  (.log js/console (str s "\n")))

(defn pr-errors [errors & [to-str :or {to-str str}]]
  (if errors
    (do (prn! "validation errors:")
        (doseq [[s x] errors]
          (prn! (str "  " (to-str x) ": " s)))
        (prn! (str "\nTotal Errors: " (count errors))))
    (prn! "Validation passed :)")))
