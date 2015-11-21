(ns rubber.core
  (:require [rethinkdb.query :as r]
            [taoensso.timbre :as timbre :refer (debug info error)]
            [org.httpkit.client :as http]
            [clojure.core.async :refer (<! >! go go-loop thread 
                                       take! <!! >!! chan close!)]))

(def search-url "http://search:9200")

(defn check-search []
  (let [status (-> @(http/get search-url) :status)]
    (if (= 200 status)
      (info "search connected ✓")
      (error "cannot connect to search @" search-url))))

(defn check-db []
  (let [conn  (r/connect :host  "rethink")]
    (when conn (info "search connected ✓"))))

(def db-conn (atom nil))

(defn get-conn []
  (if @db-conn
    @db-conn
    (let [conn (r/connect :host "rethink")]
      (info "making db connection")
      (reset! db-conn conn))))

(defn index-doc [obj]
  false)

(defn test-insert [ins]
  (-> (r/db "test")
      (r/table "test")
      (r/insert {:thingy ins})
      (r/run (get-conn))))

(defn watch-table []
  (-> (r/db "test")
      (r/table "test")
      (r/changes)
      (r/run (get-conn))))

(defn pump [chan]
  (let [cursor (watch-table)]
    (thread
      (let [realized (doall cursor)]
        (while true
               (>!! chan (first cursor)))))))

(defn test-changes []
  (let [feed (chan)
        cursor (watch-table)]

    (println "using cursor: " cursor)

    (doseq [x (range 10)]
         (do (println "inserting" x)
             (println (test-insert (str "inserted" x)))))
      
      cursor))


    ; (go-loop []
    ;          (>! feed chan)
    ;          (recur))

    ; (go-loop []
    ;          (println "CHABNGE" (<! feed))
    ;          (recur))

(defn -main [& args]
  (check-search)
  (check-db)
  (println "SUP"))


