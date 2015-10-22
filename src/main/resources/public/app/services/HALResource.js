shoppingList.factory('HALResource', [
    function(){
        var HALResource = {
            getContent : function(resource){
                var ret = [];
                if(resource._embedded){
                    for(var key in resource._embedded){
                        return resource._embedded[key];
                    }
                }
                return ret;
            },
            getResourceLinks: function (list){
                var links = [];
                for(var i = 0; i < list.length; i++){
                    links.push(list[i]._links.self.href);
                }
                return links;
            },
            getRelations : function(resource){
                var relations = [];
                if(resource._links){
                    for(var key in resource._links){
                        if(key != "self"){
                            relations.push(resource._links[key]);
                        }
                    }
                }
                return relations;
            }
        };

        return HALResource;
    }
]);