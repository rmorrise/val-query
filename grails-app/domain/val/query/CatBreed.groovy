package val.query

import grails.rest.Resource

@Resource
class CatBreed {
    String name

    static constraints = {
        name nullable: false, blank: false, maxSize: 20
    }
}
