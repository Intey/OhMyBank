(ns ombs.route
  (:require
    [compojure.api.sweet :refer :all]
    [compojure.api.middleware :refer [api-middleware]]
    [ring.util.http-response :refer :all]
    [ombs.handler.api.users :as apiu]
    [ombs.event.route :as eroute]
    ))

(defapi engine
  :middlewares [api-middleware]
  (swagger-ui)
  (swagger-docs
    {:info {:title "Bank API" :description "API for bank" }
     :tags ["api" "event" "user"] })

  (context* "/api" []
    :tags ["api"]

    eroute/events

    (context* "/users" []
              :tags [ "user"]


              (GET* "/" []
                    :summary "Return list of users")


              (POST* "/" []
                     :summary "Add new clients to bank. After this, they can participate, create events etc."
                     :body-params [{users :-
                                    (describe [apiu/User] "Usernames of paticipats.")
                                    nil}]

                     (ok (map apiu/add-user users))
                     ))

          ))
