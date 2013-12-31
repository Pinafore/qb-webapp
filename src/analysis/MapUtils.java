package analysis;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Utility functions for adding things to maps.
 *
 */
public class MapUtils
{
	public static<T> void addToMap(Map<T,Integer> map, T item)
	{
		if (!map.containsKey(item))
		{
			map.put(item,1);
		}
		else
		{
			int count = map.get(item);
			map.put(item, count+1);
		}
	}
	
	public static<T,V> void addToListMap(Map<T,List<V>> map, T key, V val)
	{
		if (!map.containsKey(key))
		{
			List<V> list = new ArrayList<V>();
			list.add(val);
			map.put(key,list);
		}
		else
		{
			map.get(key).add(val);
		}
	}
	
	public static<T,V> void addToSetMap(Map<T,Set<V>> map, T key, V val)
	{
		if (!map.containsKey(key))
		{
			Set<V> list = new HashSet<V>();
			list.add(val);
			map.put(key,list);
		}
		else
		{
			map.get(key).add(val);
		}
	}
	
	public static<T,V> void addToMapMap(Map<T,Map<V, Integer>> map, T key, V val)
	{
		if (!map.containsKey(key))
		{
			Map<V,Integer> newMap = new HashMap<V,Integer>();
			addToMap(newMap,val);
			map.put(key,newMap);
		}
		else
		{
			addToMap(map.get(key),val);
		}
	}
	
	
	// adapted from from
	// http://www.programmersheaven.com/download/49349/download.aspx
	public static<T, V> List<T> sortByValue(Map<T, V> map)
	{
		List<Map.Entry<T, V>> list = new LinkedList<Map.Entry<T, V>>(
				map.entrySet());
		Collections.sort(list, new Comparator()
		{
			public int compare(Object o1, Object o2)
			{
				return ((Comparable) ((Map.Entry) (o2)).getValue())
						.compareTo(((Map.Entry) (o1)).getValue());
			}
		});
		// logger.info(list);
		List<T> result = new ArrayList<T>();
		for (Iterator<Map.Entry<T, V>> it = list.iterator(); it.hasNext();)
		{
			Map.Entry<T, V> entry = (Map.Entry<T, V>) it.next();
			result.add(entry.getKey());
		}
		return result;
	}

}
