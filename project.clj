(defproject ombs "0.1.0-SNAPSHOT"
            :description "FIXME: write description"
            :url "http://example.com/FIXME"
            :license {:name "Eclipse Public License"
                      :url "http://www.eclipse.org/legal/epl-v10.html"}
            :dependencies [[org.clojure/clojure "1.7.0" ]
                           [org.clojure/clojure-contrib "1.2.0"] ;additions

                           [ring/ring-jetty-adapter "1.4.0"]

                           ;;[enlive "1.1.6"] ; templating(plain HTML)
                           [lib-noir "0.9.9"] ; session management
                           [clj-time "0.11.0"]

                           [org.clojure/java.jdbc "0.4.2"] ; deps fo db-driver
                           [org.xerial/sqlite-jdbc "3.8.11.2"] ; sqlite driver
                           [korma "0.4.2"] ; sql in code
                           [ragtime/ragtime.sql.files "0.3.9"] ; db migrations
                           ;; API
                           [compojure "1.4.0"] ; routing
                           [cheshire "5.5.0"] ; JSON parsing
                           [ring/ring-json "0.4.0"] ; parsing queries
                           ]
            :profiles {
                       :dev {
                             :dependencies [
                                            [speclj "3.3.1"]
                                            [clj-webdriver "0.7.2" ; this use old version of selenium-server, so
                                             :exclusions [org.seleniumhq.selenium/selenium-server]]
                                            [org.seleniumhq.selenium/selenium-server "2.49.0"] ; drive it manualy
                                            [ring/ring-mock "0.3.0"] ;;  Simplify generation of request
                                            [ring/ring-jetty-adapter "1.4.0"] ; for runs app before tests
                                            ]}

                       }

            :ragtime {:migrations ragtime.sql.files/migrations
                      :database "jdbc:sqlite:database.db" }

            :ring {:handler ombs.route/engine
                   :init ombs.db.init/database       ; use this, Luke.
                   ;:destroy ombs.handler/destroy
                   :nrepl {:start? true
                           :port 3001 }
                   }


            :plugins [ [lein-ring "0.8.8"]
                      [speclj "3.3.0"]
                      [lein-ancient "0.6.6"]
                      [ragtime/ragtime.lein "0.3.8"]
                      [lein-gossip "0.1.0-SNAPSHOT"]
                      [cider/cider-nrepl "0.9.1"]
                      ]

            ;; :test-paths ["spec"]

            )
