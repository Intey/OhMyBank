(defproject ombs "0.1.0-SNAPSHOT"
            :description "FIXME: write description"
            :url "http://example.com/FIXME"
            :license {:name "Eclipse Public License"
                      :url "http://www.eclipse.org/legal/epl-v10.html"}
            :dependencies [[org.clojure/clojure "1.7.0" ]
                           [org.clojure/clojure-contrib "1.2.0"] ;additions
                           [org.clojure/clojurescript "0.0-3308"]
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

                           [org.omcljs/om "0.9.0"] ; react
                           [prismatic/om-tools "0.3.10"]
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
                   :init ombs.db.init/database
                   ;:destroy ombs.handler/destroy
                   }

            :plugins [ [lein-ring "0.8.8"]
                      [lein-cljsbuild "1.0.6"]
                      [speclj "3.3.0"]
                      [lein-ancient "0.6.6"]
                      [ragtime/ragtime.lein "0.3.8"]
                      [lein-gossip "0.1.0-SNAPSHOT"]
                      [cider/cider-nrepl "0.9.1"]
                      ]

            :eval-in :nrepl
            :test-paths ["spec"]

          :cljsbuild
          {:builds
		   [{:id "devel"
			 :source-paths ["src/cljs"]
			 :compiler {:output-to "resources/public/js/app.js"
						:output-dir "resources/public/js/out-devel"
						:source-map true
						:optimizations :none
						:cache-analysis false
						:asset-path "/static/js/out-devel"
						:main ombs.frontcore
						:pretty-print true}}
			{:id "prod"
			 :source-paths ["src/cljs"]
			 :compiler {:output-to "resources/public/js/app.js"
						:output-dir "resources/public/js/out-prod"
						:source-map "resources/public/js/app.js.map"
						:optimizations :advanced
						:cache-analysis false
						:asset-path "/static/js/out-prod"
						:main ombs.frontcore
						:pretty-print false}}]}
)
