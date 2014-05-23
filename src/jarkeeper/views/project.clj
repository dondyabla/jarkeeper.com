(ns jarkeeper.views.project
  (:require [clojure.string :as string]
            [jarkeeper.views.common :as common-views]
            [hiccup.core :refer [html]]
            [hiccup.page :refer [html5 include-css include-js]]
            [hiccup.util :refer [escape-html]]))



(defn- render-deps [deps]
  (for [dep deps]
    [:tr
     [:td (first dep)]
     [:td (second dep)]
     [:td (str (first (last dep)))]
     [:td.status-column
       (if (nil? (last dep))
         [:span.status.up-to-date {:title "Up to date"}]
         [:span.status.out-of-date {:title "Out of date"}])]]))


(defn- render-stats [stats]
  [:section.summary.row
   [:ul
    [:li.small-12.large-4.columns
     [:span.number (:total stats)]
     [:span.stats-label "dependencies"]]
    [:li.small-12.large-4.columns
     [:span.status.up-to-date]
     [:span.number (:up-to-date stats)]
     [:span.stats-label "up to date"]]
    [:li.small-12.large-4.columns
     [:span.status.out-of-date]
     [:span.number (:out-of-date stats)]
     [:span.stats-label "out of date"]]]])

(defn- render-profile [profile]
  [:table.small-12.columns
    [:thead
     [:tr
      [:th (name (first profile))]
      [:th {:width "180"} ""]
      [:th {:width "180"} ""]
      [:th {:width "90"} ""]]]
   (render-deps (second profile))])



(defn index [project]
  (html5 {:lang "en"}
    [:head
     [:title (str "Jarkeeper: " (:name project))]
     (common-views/common-head)
     (common-views/ga)
     (include-css "/app.css")]
    [:body
      (common-views/header)
      [:article.project-content
        [:header.row
         [:h1
           [:a {:href (:github-url project)} (:name project)]
           [:span.version (:version project)]]
         [:h2 (:description project)]
         (if (> (:out-of-date (:stats project)) 0)
           [:img {:src "/images/out-of-date.png"}]
           [:img {:src "/images/up-to-date.png"}])]
        (render-stats (:stats project))
        [:section.dependencies.row
          [:table.small-12.columns
            [:thead
             [:tr
              [:th "Dependency"]
              [:th {:width "180"} "Used"]
              [:th {:width "180"} "Latest"]
              [:th {:width "90"} "Status"]]]
            (render-deps (:deps project))]
         (for [profile (:profiles project)]
           (if (first profile)
             (html
               (render-stats (nth profile 2))
               (render-profile profile))))]

       [:section.installation-instructions.row
        [:h2 "Markdown"]
        [:code
           (str "[![Dependencies Status]"
                "(http://jarkeeper.com/"
                (:repo-owner project)
                "/"
                (:repo-name project)
                "/status.png)](http://jarkeeper.com/"
                (:repo-owner project)
                "/"
                (:repo-name project)
                ")")]
        [:h2 "HTML"]
        [:code
           (escape-html (str "<a href=\""
                "http://jarkeeper.com/"
                (:repo-owner project)
                "/"
                (:repo-name project)
                "\" title=\"Dependencies status\"><img src=\"http://jarkeeper.com/"
                (:repo-owner project)
                "/"
                (:repo-name project)
                "/status.png\"></a>"))]
        ]]
     (common-views/common-footer)
    ]))
