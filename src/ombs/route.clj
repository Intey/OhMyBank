(ns ombs.route
  (:require
    [ring.middleware.params :refer [wrap-params]]
    [ring.middleware.nested-params :refer [wrap-nested-params]]

    [compojure.api.sweet :refer :all]
    [ring.util.http-response :refer :all]
	[schema.core :as s]
    [clojure.pprint :refer [pprint]]

    [ombs.handler.api.events :as apie]
    [ombs.handler.api.users :as apiu]
    ))


;;(s/defschema Date org.joda.time.LocalDate )

(s/defschema Event
  {:id s/Int
   :author s/Str
   :name s/Str
   :date s/Str
   :price s/Num
   :rest s/Num
   :parts s/Num
   :status s/Str
   }
  )
(s/defschema InEvent
  {:author s/Str
   :name s/Str
   :date s/Str
   :price s/Num})


(defapi engine
  (swagger-ui)
  (swagger-docs
    {:info {:title "Bank API" :description "API for bank" }
     :tags ["api" "event" "user"] }

    )
  (context* "/api" []
            :tags ["api"]
            (context* "/events" []
                      :tags ["event"]
                      (GET* "/" []
                            :summary "Return events list"
                            :query-params [{types :-
                                            (describe [(s/enum :initial :finished :in-progress)]
                                              (str "Select events by types. Optional."
                                                   "Use query string: /api/events?types=initial&types=finished."))
                                            nil}]
                            (ok (apie/get-events types) ))
                      (POST* "/" []
                             :summary "Add event or events."
                             :body-params [events :- (describe [InEvent] "Event data")
                                           {participants :-  (describe [apiu/User] "nicknames of paticipats.") nil}
                                           ]
                             (ok (map apie/new-event events participants))
                             ))
            (context* "/users" []
                      :tags [ "user"]
                      (GET* "/" []
                            :summary "return list of users")
                      (POST* "/" []
                             :summary "Add new clients to bank. After this, they can participate, create events etc."
                             :body-params [{users :- (describe [apiu/User] "nicknames of paticipats.") nil}]

                             (ok (map  users))
                             )
                      )

            ))
;; (-> engine
;;     (wrap-routes wrap-nested-params)
;;     (wrap-routes wrap-params))
