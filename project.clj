(defproject ombs "0.1.0-SNAPSHOT"
            :description "FIXME: write description"
            :url "http://example.com/FIXME"
            :license {:name "Eclipse Public License"
                      :url "http://www.eclipse.org/legal/epl-v10.html"}
            :dependencies [[org.clojure/clojure "1.7.0" ]
                           [org.clojure/clojure-contrib "1.2.0"] ;additions
                           [ring/ring-jetty-adapter "1.3.2"]
                           [compojure "1.3.3"] ; routing
                           [enlive "1.1.6"] ; templating(plain HTML)
                           [lib-noir "0.9.9"] ; session management
                           [liberator "0.12.2"] ; for RESTful project
                           [cheshire "5.4.0"] ; JSON parsing
                           [clj-time "0.10.0"]
                           [org.clojure/java.jdbc "0.3.6"] ; deps fo db-driver
                           [org.xerial/sqlite-jdbc "3.8.7"] ; sqlite driver
                           [korma "0.4.0"] ; sql in code
                           [ragtime/ragtime.sql.files "0.3.8"] ; db migrations
                           ]
            :profiles {
                       :dev { 
                             :dependencies [
                                            [speclj "3.3.0"]
                                            [clj-webdriver "0.6.1" ; this use old version of selenium-server, so
                                             :exclusions [org.seleniumhq.selenium/selenium-server]]
                                            [org.seleniumhq.selenium/selenium-server "2.45.0"] ; drive it manualy
                                            [ring-mock "0.1.5"] ; ??
                                            [ring/ring-jetty-adapter "1.3.2"] ; for runs app before tests
                                            ]}

                       }

            :ragtime {:migrations ragtime.sql.files/migrations
                      :database "jdbc:sqlite:database.db" }

            :ring {:handler ombs.route/engine
                   :init ombs.db.init/database       ; use this, Luke.
                   ;:destroy ombs.handler/destroy
                   } 

            :plugins [ [lein-ring "0.8.8"]
                      [speclj "3.3.0"]
                      [lein-ancient "0.6.6"]
                      [ragtime/ragtime.lein "0.3.8"]
                      [lein-gossip "0.1.0-SNAPSHOT"]
                      [cider/cider-nrepl "0.9.1"]
                      ]

            ;:test-paths ["spec"]

            )
