/**
Copyright (c) 2007-2013 Alysson Bessani, Eduardo Alchieri, Paulo Sousa, and the authors indicated in the @author tags

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package bftsmart.app.shoppingcart;

import java.util.TreeMap;
import java.util.Map;

import java.io.Serializable;

/**
 *
 * @description: operations provided by the data strucutre
 */
public class MapOfCarts implements Serializable {
	// key: cartName
	// Value: Map<String product_id, byte[] amount>
	private static final long serialVersionUID = -8898539992606449057L;
	
	private Map<String, Map<String,byte[]>> cartMap = null;

	public MapOfCarts() {
		cartMap=new TreeMap<String, Map<String,byte[]>>();
	}


	public Map<String,byte[]> addCart(String key, Map<String, byte[]> value) { //add new cart into the map
		return cartMap.put(key, value);
	}


	public byte[] addProduct(String cartName, String key, byte[] value) { //add new product into the existing shopping cart
		Map<String,byte[]> cart = cartMap.get(cartName);
		if (cart == null) {
            System.out.println("Shopping Cart Not Found: "+cartName);
            return null;
        }
		byte[] ret = cart.put(key, value);
		return ret;
	}

	public Map<String,byte[]> getCart(String cartName) {
		return cartMap.get(cartName);
	} //get cart by its name

	public byte[] getEntry(String cartName, String key) {
		System.out.println("Cart name: "+cartName);
		System.out.println("Product key: "+ key);
		Map<String,byte[]> curCart = cartMap.get(cartName);
		if (curCart == null) {
                    System.out.println("Cart Does Not Found: " + cartName);
                    return null;
                }
		return curCart.get(key);
	}

	public int getNumOfCarts() {
		return cartMap.size();
	} //return the size of the cartMap

	public int getSize(String cartName) { // get the size of one specific cart (by its name)
		Map<String,byte[]> cart = cartMap.get(cartName);
		return (cart == null)?0:cart.size();
	}

	public Map<String,byte[]> removeCart(String cartName) { //remove the cart by its name
		Map<String, byte[]> curCart = cartMap.get(cartName);
		if(curCart == null) {
			System.out.println("Cart Does Not Exist: "+cartName);
			return null;
		}
		System.out.println("Cart Deleted :" + cartName);
		return cartMap.remove(cartName);
	} //remove the

	public byte[] removeEntry(String cartName,String key) { //remove the item in one specific cart
		Map<String,byte[]> curCart= cartMap.get(cartName);
		byte[] ret = curCart.remove(key);
		if(ret == null){
			System.out.println("Key Not Found: "+key);
		}
		System.out.println("Item With Key: " + key + " Is Removed");
		return ret;
	}
}
