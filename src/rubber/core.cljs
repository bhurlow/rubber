(ns rubber.core
  (:require-macros [rubber.macros :refer [boob]])
  (:require-macros  [cljs.core.async.macros :refer  [go]])
  (:require [cljs.nodejs :as nodejs]
            [cljs.core.async :refer  [put! chan <!]]
            [taoensso.timbre :as log :refer-macros  [info infof debugf error errorf]]))

(nodejs/enable-util-print!)

(def search-url "http://192.168.99.100:9200/")

(defonce r (nodejs/require "rethinkdb"))
(defonce request (nodejs/require "superagent"))

(def db-conn (atom nil))

(defn get-conn []
  (let [c (chan)]
    (-> (.connect r "192.168.99.100")
        (.then (fn [conn]
               (put! c conn))))
    c))

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

(defn watch-query []
 (-> (.db r "ifriend")
     (.table "boob")
     (.changes)))

(defn handle-feed [feed]
  (println "iterating" feed)
  (.each feed (fn [err el] (println "GOT ONE " el))))

(defn watch-table []
  (go
    (-> (<! (run (watch-query) (<! (get-conn))))
        handle-feed))
  :pending-changes-feed)

(defn -main  [& args]
    (info "Starting trender...")
    (do-query))

(set! *main-cli-fn* -main)

;; ===== ES SEARCH INDEXING =====

(defn getr []
  (let [c (chan)]
    (go (-> (.get request search-url)
            (.end (fn [err res]
                  (put! c (js->clj res.body :keywordize-keys true))))))
    c))

(defn index-user []
  (go
    (let [body (<! (run (user-query) (<! (get-conn))))]
      (-> (.put request (str search-url "frog/dog/1"))
          (.send body)
          (.set "Accept" "application/json")
          (.end (fn [err res]
                  (println err)
                  (println "POST RESPONSE" res.status)))))))

