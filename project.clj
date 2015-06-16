(defproject ombs "0.1.0-SNAPSHOT"
            :description "FIXME: write description"
            :url "http://example.com/FIXME"
            :license {:name "Eclipse Public License"
                      :url "http://www.eclipse.org/legal/epl-v10.html"}
            :dependencies [[org.clojure/clojure "1.6.0" ]
                           ;[org.clojure/core.memoize "0.5.7"] ; noir-middleware dependence
                           ;[org.clojure/math.numeric-tower "0.0.4"] ; math funcs
                           [org.clojure/clojure-contrib "1.2.0"] ;additions
                           [info.sunng/ring-jetty9-adapter "0.8.4"]                          
                           ;[ring/ring-jetty-adapter "1.3.2"]
                           [http-kit "2.0.0"]
                           ;[info.sunng/ring-jetty9-adapter "0.8.4"]
                           [compojure "1.3.4"] ; routing
                           [enlive "1.1.5"] ; templating(plain HTML)
                           [lib-noir "0.9.9"] ; session management
                           [liberator "0.13"] ; for RESTful project
                           [cheshire "5.5.0"] ; JSON parsing

                           [org.clojure/java.jdbc "0.3.7"] ; deps fo db-driver
                           [org.xerial/sqlite-jdbc "3.8.10.1"] ; sqlite driver
                           [korma "0.4.2"] ; sql in code
                           [ragtime/ragtime.sql.files "0.3.9"] ; db migrations



                           ;[speclj "3.2.0"] ; bdd tests - core
                           ;[clj-webdriver "0.6.1" ; this use old version of selenium-server, so
                           ; :exclusions [org.seleniumhq.selenium/selenium-server]]
                           ;[org.seleniumhq.selenium/selenium-server "2.46.0"] ; drive it manualy
                           ;[ring-mock "0.1.5"] ; ??
                           ]


            :main ombs.core
            :plugins [ [lein-ring "0.8.8"]
                      [lein-ancient "0.6.6"]
                      [ragtime/ragtime.lein "0.3.8"]
                      [speclj "3.2.0"] ]
            :test-paths ["spec"]
            :ragtime {:migrations ragtime.sql.files/migrations
                      :database "jdbc:sqlite:database.db" }
            :ring {:handler ombs.route/engine
                   ;:init ombs.handler/init       ; use this, Luke.
                   ;:destroy ombs.handler/destroy
                   })
