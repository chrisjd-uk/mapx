(ns mapx.core
  (:require [clojure.set :as set]))

(defn map-xform
  "Takes an input map and transforms it according to the rules
  described in xform.  xform is a map of keys :select, :delete,
  :update and :rename.  The transformations are applied specifically
  in this order -- keys are selected, deleted, updated and renamed
  -- and all are optional.

  :select - A seq of keys to select from the input map.
  :delete - A seq of keys to delete from the input.
  :update - A map from keys in the input map to functions to apply to
            the corresponding values using update.
  :rename - A map from old keys to new keys."
  [m & {:as xform}]
  (cond-> m
    (contains? xform :select) (select-keys (:select xform))
    (contains? xform :delete) (#(reduce dissoc % (:delete xform)))
    (contains? xform :update) (#(reduce (fn [m [k f]]
                                          (update m k f))
                                        %
                                        (:update xform)))
    (contains? xform :rename) (set/rename-keys (:rename xform))))

(defn projection
  "Does a select and rename in one operation.  The keys in the rename
  map are used as the selection seq, then the map itself is used to
  rename keys."
  [m rename-m]
  (map-xform m
             :select (keys rename-m)
             :rename rename-m))
  
