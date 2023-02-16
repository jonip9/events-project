(defproject events-ring-server "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :license {:name "EPL-2.0 OR GPL-2.0-or-later WITH Classpath-exception-2.0"
            :url "https://www.eclipse.org/legal/epl-2.0/"}
  :dependencies [[org.clojure/clojure "1.11.1"]
                 [ring/ring-core "1.9.6"]
                 [ring/ring-jetty-adapter "1.9.6"]
                 [ring/ring-json "0.5.1"]
                 [ring/ring-defaults "0.3.4"]
                 [ring/ring-devel "1.9.6"]
                 [compojure "1.7.0"]
                 [com.walmartlabs/lacinia "1.2-alpha-3"]
                 [com.github.seancorfield/next.jdbc "1.3.847"]
                 [org.postgresql/postgresql "42.3.7"]
                 [hikari-cp "3.0.0"]]
  :ring {:handler events-ring-server.core/app}
  :plugins [[lein-ring "0.12.6"]]
  :repl-options {:init-ns events-ring-server.core})
