import de.tud.stg.tigerseye.example.dzoneunits.GroovyDefinitionForUnitsDSL
import org.jscience.physics.amount.Amount
import javax.measure.quantity.Velocity
import javax.measure.unit.Unit
import de.tud.stg.tigerseye.example.dzoneunits.UnitBinding

GroovyDefinitionForUnitsDSL.enableUnits()
// use the script binding for retrieving unit references
binding = new UnitBinding()

println( 2.kg )
println( 3.m )
println( 4.5.in )

def a = new Amount<Velocity>()


// arithmetics: multiply, divide, addition, subtraction, power
println( 18.4.kg * 2 )
println( 1800000.kg / 3 )
println( 1.kg * 2 + 3.kg / 4 )
println( 3.cm + 12.m * 3 - 1.km )
println( 1.5.h + 33.s - 12.min )
println( 30.m**2 - 100.ft**2 )

// opposite and comparison
println( -3.h )
println( 3.h < 4.h )

//The following will throw an error because kilogram and hour are not comparable Amounts.
try{
	println( 3.h < 4.kg )
}catch (all){
	println "caught exception: $all"
}


// inverse units
println( 30.km/h + 2.m/s * 2 )
println( 3 * 3.mg/L )
println( 1/2.s - 2.Hz )

// unit conversion
println( 200.cm.to(ft) )
println( 1.in.to(cm) )
