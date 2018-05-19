import org.junit.Test;

public class ClassTypeTest {

	@Test
	public void test(){
		Class<?> longType = Long.class;
		Class<?> stringType = String.class;

		//assert(longType == stringType);
		assert(longType == longType);
		System.out.println(longType == longType);
		
	}
	
}
