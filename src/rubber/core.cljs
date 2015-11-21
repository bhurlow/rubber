(ns rubber.core
  (:require-macros  [cljs.core.async.macros :refer  [go]]
                    [rubber.macros :refer [boob bike]] :reload)
  (:require [cljs.nodejs :as nodejs]
            [cljs.core.async :refer  [put! chan <!]]
            [taoensso.timbre :as log :refer-macros  [info infof debugf error errorf]]))

(nodejs/enable-util-print!)

(defn rl [] (require 'rubber.core :reload))

(def search-url "http://search:9200")
(defonce r (nodejs/require "rethinkdb"))
(defonce request (nodejs/require "superagent"))

(def db-conn (atom nil))

(defn get-search [cb]
  (-> (.get request search-url)
      (.set "Accept" "application/json")
      (.end (fn [err res] (cb err res)))))

(defn get-db []
  (-> (.connect r "rethink")
      (.then (fn [conn]
               (println conn)
               (if conn 
                 (info "db connection good :)")
                 (error "no db connection!"))))))

(defn test-connections []
  (info "testing connections")
  (get-search (fn [err res] 
                (if (= 200 res.status)
                  (do (info "search connection good :)") (get-db))
                  (error "cant connect to search")))))

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

;; ===== ES SEARCH INDEXING =====

(defn getr []
  (let [c (chan)]
    (go (-> (.get request search-url)
            (.end (fn [err res]
                   (put! c (js->clj res.body :keywordize-keys true))))))
    c))

(defn db-run [query]
  (<! (run query (<! (get-conn)))))

(defn test-index []
  (go
    (-> (<! (run (user-query) (<! (get-conn))))
        (js->clj :keywordize-keys true)
        (index-obj :user))))

(defn index-obj [obj table]
  (println "indexing" (:id obj) "in table:" table)
  (go
    (-> (.put request (str search-url "ifriend/" (name table) "/" (:id obj)))
        (.send (clj->js obj))
        (.set "Accept" "application/json")
        (.end (fn [err res]
                (println err)
                (println "POST RESPONSE" res.status))))))

(defn -main  [& args]
    (info "Starting Rubber ...")
    (test-connections)
    (do-query))

(set! *main-cli-fn* -main)
