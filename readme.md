# RepositoryMapping

### Reading
The EntityFinder can read all Data from the Database which is described with the Annotation @Entity, @EntityValue and @OneToOne.
For instance:

```
@Entity(table = "Person", alias = Person.TABLE_ALIAS)
public class Person {
    public static final String TABLE_ALIAS = "p";
    
    @EntityValue(value = "PersonId", type = Sql.Types.INT, primary = true)
    private Integer personId;
    
    @EntityValue(value = "FirstName")
    private String firstName;
    
    @EntityValue(value = "LastName")
    private String lastName;
    
    @EntityValue(value = "AddressName")
    private String addressName;
    
    @EntityValue(value = "AddressNumber", type = Sql.Types.INT)
    private Integer addressNumber;
    
    @OneToOne(primaryKey = "CityId", foreignKey = "CityKey")
    private City city;
}
```

@Entity: Describes the table with an alias

@EntityValue: Describes the column of the table, the type of data and whether it is a primary key

@OneToOne: References the foreign key to another Entity