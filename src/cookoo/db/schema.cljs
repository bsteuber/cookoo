(ns cookoo.db.schema
  (:require [cookoo.db.core :refer [fact! class! enum! validator!]]
            [cookoo.db.indexers :as idx]
            [cookoo.db.knowledge-base :as kb]
  	    [cookoo.db.uid :refer [id?]]
            [cookoo.tools.debug :refer [fail log]]
            [cookoo.tools.validate :refer [validator]]))  

(extend-type js/Function
  IPrintWithWriter
  (-pr-writer [a writer opts]
    (-write writer "#<Fn>")))

(defn init-schema []
  (class! :Value "Value" :Value)
  (class! :Host-val "Host value" :Value :Validated)
  (validator! :str-v "String validator" string?)
  (validator! :fn-v "Function validator" ifn?)
  (validator! :num-v "Number validator" number?)
  (validator! :obj-v "Object validator" id?)
  (class! :Host-str "Host string" :Host-value
          [:validator :str-v])
  (class! :Host-fn "Host function" :Host-value
          [:validator :fn-v])
  (class! :Host-num "Host number" :Host-value
          [:validator :num-v])
  (class! :Object "Object" :Value :Validated
          [[:class "class" :Class]] 
          [:validator :obj-v])
  (class! :Titled "Titled" :Value
          [[:title "title" :String]])
  (class! :Inherits "Inherits" :Object
          [[:inherit "inherit" :Object]])
  (class! :Class "Class" [:Titled :Inherits] :Metaclass
          [[:has-attr "has attribute" :Class 
            {:index :attr-owner}]
           [:instance "instance" :Object
            {:index :class}]])
  (class! :Metaclass "Metaclass" :Class :Metaclass)  
  (class! :Validated "Validated" :Class :Metaclass
          [[:validator "validators" :HostFn]])            
  (class! :Validator "Validator" :Titled
          [[:pred "validator predicate" :HostFn]])  
  (class! :Attr "Attribute" [:Titled :Object] 
          [[:attr-owner "attribute owner" :Class]
           [:attr-class "attribute class" :Class]
           [:attr-reader "attribute reader" :HostFn]
           [:index "index" :Index {:index :source-attr} ]])
  (class! :Index "Index" :Object
          [[:source-attr "source attribute" :Attr]
           [:target-attr "target attribute" :Attr]
           [:indexer "indexer function" :HostFn]])
  (class! :Has-default "Has default" :Attr
          [[:default "default value" :Value [:slot-type :optional]]])
  (class! :DbAttr "DB Attribute" :Has-default)
  (class! :IndexAttr "Index Attribute" :Atrr)
  (class! :Enum "Enumeration" :Titled))
