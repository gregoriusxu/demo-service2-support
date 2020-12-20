/* 
 * create by 2020年2月3日 下午12:38:09
 */
package com.demo2.support.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.demo2.support.cache.BasicCache;
import com.demo2.support.dao.BasicDao;
import com.demo2.support.entity.Entity;

/**
 * The DDD repository using cache: 
 * 1) if load an entity, load from cache first.
 * 2) if not in the cache, set to cache after query from database.
 * 3) if update or delete an entity, delete from cache.
 * @author fangang
 */
public class RepositoryWithCache extends Repository implements BasicDao {
	private BasicCache cache;

	/**
	 * @return the cache
	 */
	public BasicCache getCache() {
		return cache;
	}

	/**
	 * @param cache the cache to set
	 */
	public void setCache(BasicCache cache) {
		this.cache = cache;
	}

	@Override
	public <S extends Serializable, T extends Entity<S>> T load(S id, T template) {
		T entity = cache.get(id, template);
		if(entity!=null) return entity;
		entity = super.load(id, template);
		cache.set(entity);
		return entity;
	}

	@Override
	public <S extends Serializable, T extends Entity<S>> List<T> loadForList(Collection<S> ids, T template) {
		if(ids==null||template==null) return null;
		
		Collection<T> entities = cache.getForList(ids, template);
		List<S> otherIds = getIdsNotInCache(ids, entities);
		if(otherIds.isEmpty()) return (List<T>)entities; //cache get all of the entities.
		
		List<T> list = super.loadForList(otherIds, template);
		
		if(otherIds.size()==ids.size()) return list; //all of the entities query for database.
		return (List<T>)fillOtherEntitiesIn(ids, entities, list); //fill the entity query for db in the list of entities get in cache.
	}
	
	/**
	 * @param ids
	 * @param entities
	 * @return all of the id not in cache
	 */
	private <S extends Serializable, T extends Entity<S>> List<S> getIdsNotInCache(Collection<S> ids, Collection<T> entities) {
		List<S> otherIds = new ArrayList<>();
		Iterator<T> entity = entities.iterator();
		Iterator<S> id = ids.iterator();
		while(entity.hasNext()&&id.hasNext()) {
			if(entity.next()==null) otherIds.add(id.next());
		}
		return otherIds;
	}
	
	/**
	 * fill the entities, which load from other source, in the list of entities load from cache.
	 * @param ids
	 * @param entities the list of entities load from cache
	 * @param otherEntities the other entities load from other source
	 * @return the list of entities
	 */
	private <S extends Serializable, T extends Entity<S>> 
			Collection<T> fillOtherEntitiesIn(Collection<S> ids, Collection<T> entities, Collection<T> otherEntities) {
		Map<S,T> map = new HashMap<>();
		for(T value : otherEntities) {
			map.put(value.getId(), value);
		}
		
		Iterator<T> voIter = entities.iterator();
		Iterator<S> idIter = ids.iterator();
		while(voIter.hasNext()&&idIter.hasNext()) {
			T entity = voIter.next();
			if(entity!=null) continue;
			entity = map.get(idIter.next());
		}
		return entities;
	}

	@Override
	public <T> void update(T entity) {
		super.update(entity);
		if(entity instanceof Entity) 
			deleteCache((Entity<?>) entity);
	}

	/**
	 * @param entity the entity
	 */
	private <S extends Serializable, T extends Entity<S>> void deleteCache(T entity) {
		cache.delete(entity.getId(), entity);
	}

	@Override
	public <T> void insertOrUpdate(T entity) {
		super.insertOrUpdate(entity);
		if(entity instanceof Entity) 
			deleteCache((Entity<?>) entity);
	}

	@Override
	public <S extends Serializable, T extends Entity<S>> void delete(S id, T template) {
		super.delete(id, template);
		cache.delete(id, template);
	}

	@Override
	public <T> void delete(T entity) {
		super.delete(entity);
		if(entity instanceof Entity) 
			deleteCache((Entity<?>) entity);
	}

	@Override
	public <S extends Serializable, T extends Entity<S>> void deleteForList(Collection<S> ids, T template) {
		super.deleteForList(ids, template);
		cache.deleteForList(ids, template);
	}
}
