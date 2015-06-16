(ns ombs.core
  (:require 
    [ring.middleware.reload :refer [wrap-reload]] 
    ;[ring.adapter.jetty9 :refer [run-jetty]]
    [org.httpkit.server :refer [run-server]]
    [ombs.route :refer [engine ]]))

(def ws-handler
  {:on-connect (fn [ws] (println "opened"))
   :on-close (fn [ws status reason] (println "closed"))
   :on-error (fn [ws e] (println "error"))
   :on-text (fn [ws msg]
              ;(send! ws msg)
              (println "text")
              )
   :on-byte (fn [ws bytes offset length] 
              (println "bytes")
              ) } )

(def in-dev? false) ;; TODO read a config variable from command line, env, or file?

(defn -main [& args] ;; entry point, lein run will pick up and start from here
  (let [handler (if in-dev? 
                  (wrap-reload engine) ;; only reload when dev
                  engine)]
    (run-server handler {:port 8080})))
