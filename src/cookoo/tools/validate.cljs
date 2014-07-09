(ns cookoo.tools.validate)

(def conjv (comp vec conj))

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

(defn validate [validators x]
  (reset! validate-state {})
  (doseq [val (flatten [validators])]
    (val x))
  (or (get! [:errors])
      :ok))
