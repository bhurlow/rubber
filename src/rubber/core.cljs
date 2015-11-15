(ns rubber.core
  (:require-macros [rubber.macros :refer [boob]])
  (:require-macros  [cljs.core.async.macros :refer  [go]])
  (:require [cljs.nodejs :as nodejs]
            [cljs-promises.core :as p]
            [cljs.core.async :refer  [put! chan <!]]
            [taoensso.timbre :as log :refer-macros  [info infof debugf error errorf]]))

(nodejs/enable-util-print!)

(defn -main  [& args]
    (info "Starting trender..."))

(set! *main-cli-fn* -main)

(println "HERE I AM")

(defn boo []
  (println "kaboo"))

(defonce r (nodejs/require  "rethinkdb"))

(defn get-conn []
  (let [c (chan)]
    (-> (.connect r "192.168.99.100")
        (.then (fn [conn] (put! c conn))))
    c))

(def thingy 9999)

(defn user-query []
  (-> (.db r "ifriend")
      (.table "user")
      (.get "0081be90-7972-449b-aca9-57548b696a0f")))

(defn run [query conn]
  (let [c (chan)]
    (.then
      (.run query conn) 
      (fn [res] (put! c res)))
    c))

(defn do-query []
  (go 
    (-> (<! (run (user-query) (<! (get-conn))))
        (js->clj :keywordize-keys true)
        (select-keys [:name :pass])
        (println)))
  :pending)

