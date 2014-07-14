(ns cookoo.db.schema
  (:require [cookoo.db.core :refer [fact! class! enum! validator!]]
            [cookoo.db.indexers :as idx]
            [cookoo.db.knowledge-base :as kb]
  	    [cookoo.db.uid :refer [id?]]
            [cookoo.tools.debug :refer [fail log]]
            [cookoo.tools.validate :refer [validator]]))  

(declare str-v fn-v num-v obj-v
         Host-type Host Host-str Host-fn Host-num
         Class-c Object-c

         Callable-c Function-c
         Boxed-type Boxed-val Boxed-str Boxed-fn)

(defn host-schema []
  (def str-v
    (db/validator! "String validator" string?))
  (def fn-v
    (db/validator! "Function validator" ifn?))
  (def num-v
    (db/validator! "Number validator" number?))
  (def obj-v
    (db/validator! "Object validator" id?))
  (class! :Host-type "Host type" :Validated)
  (class! :Host-val "Host value" :Value :Host-type)
  (class! :Host-str "Host string" :Host-value
          [:validator :str-v])
  (class! :Host-fn "Host function" :Host-value
          [:validator :fn-v])
  (class! :Host-num "Host number" :Host-value
          [:validator :num-v])

)

(defn host-type! [name pred])

(defn init []
)


(defn function-schema []
  (class! :Callable "Callable" :Value :Metaclass
          [[:call "call" :Host-fn]])
  (class! :Function "Function" :Value :Callable
          [[:args "argument list" :Arg-list]
           [:return "return class" :Class]])
)

(defn boxed-schema []
  (class! :Boxed-type "Boxed type" :Validated :Metaclass)
  (class! :Boxed-val "Boxed host value" :Value :Boxed-type
          [[:host-val "host value" :Host-val]])
  (class! :Boxed-Function "Host function" :Function
          
          [[]]))



(defn init-schema []
  (class! :Value "Value" :Value)
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
