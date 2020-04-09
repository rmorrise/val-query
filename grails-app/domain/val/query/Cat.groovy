package val.query

import grails.rest.Resource
import org.hibernate.FlushMode
import org.hibernate.Session

@Resource
class Cat {
    static belongsTo = [breed: CatBreed]

    String name
    String owner

    static constraints = {
        name nullable: false, blank: false, maxSize: 20
        owner nullable: false, blank: false, maxSize: 50
        breed nullable: false,
            validator: { val, Cat obj ->
                def duplicateRecordCount
                try {
                    duplicateRecordCount = Cat.withNewSession { Session session ->
                        session.flushMode = FlushMode.MANUAL

                        def queryCount = Cat.createCriteria().get {
                            projections {
                                count('id')
                            }
                            if (obj.id) {
                                not {
                                    idEq(obj.id)
                                }
                            }
                            eq('name', obj.name)
                            eq('owner', obj.owner)
                            breed {
                                idEq(obj.breed.id)
                            }
                        }
                        queryCount
                    }
                }
                catch (e) {
                    log.error("Error checking for duplicate PreferenceSetting. [msg=${e.message}]", e)
                    throw e
                }
                if (duplicateRecordCount > 0) {
                    return 'preferenceSettings.preferenceDefinition.duplicateRecord'
                }
            }
    }
}
