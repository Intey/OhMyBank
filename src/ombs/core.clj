(ns ombs.core 
  (:require 
    [clj-redis.client :as redis]
    [ombs.views :as views]
    ))

;(:use [ring.util.response])
; (:use [ring.adapter.jetty :only [run-jetty]]) ; for what ? 

(def db (redis/init {:url "redis://localhost:6379"}))

(defn parse-input [a]
  (Integer/parseInt a))

(defn summary [value]
  "Handler. calc data and return template with data"
  (let [old (redis/get db "value")]
    (try (let [a (parse-input value) b (parse-input old)]
           (redis/set db "value" value)
           (views/page-summary {:sum (+ a b) :old old}))
      (catch NumberFormatException e
        (views/page-summary {:old old :error "Number Format Exception"})))))

(defn index []
  "Handler. show index page"
  (let [old (redis/get db "value")]
    (views/page-index {:old old})))
