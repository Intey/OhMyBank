(defproject ombs "0.1.0-SNAPSHOT"
            :description "FIXME: write description"
            :url "http://example.com/FIXME"
            :license {:name "Eclipse Public License"
                      :url "http://www.eclipse.org/legal/epl-v10.html"}
            :dependencies [[org.clojure/clojure "1.6.0"]
                           [org.clojure/clojure-contrib "1.2.0"]
                           [ring/ring-jetty-adapter "1.3.2"]
                           [compojure "1.3.3"]
                           [clj-redis "0.0.12"]
                           [enlive "1.1.5"]
                           [org.clojure/java.jdbc "0.3.6"]
                           [org.xerial/sqlite-jdbc "3.8.7"]
                           [korma "0.4.0"]
                           [ragtime/ragtime.sql.files "0.3.8"]
                           ;for what ? best loggining ? yeah ? 
                           [log4j "1.2.17" :exclusions [javax.mail/mail
                                                        javax.jms/jms
                                                        com.sun.jdmk/jmxtools
                                                        com.sun.jmx/jmxri]]]
            :plugins [ [lein-ring "0.8.8"]
                      [lein-ancient "0.6.6"]
                      [ragtime/ragtime.lein "0.3.8"] ]
            :ragtime {:migrations ragtime.sql.files/migrations
                      :database "jdbc:sqlite:database.db" }
            :ring {:handler ombs.routes/engine})
