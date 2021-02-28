/**
 * 
 */
package com.demo2.support.repository;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.demo2.support.dao.BasicDao;
import com.demo2.support.dao.impl.factory.Join;
import com.demo2.support.entity.Entity;
import com.demo2.support.exception.OrmException;
import com.demo2.support.utils.BeanUtils;
import com.demo2.support.utils.EntityUtils;

/**
 * The generic ddd factory to load and assemble domain objects together,
 * according to vObj.xml
 * 
 * @author fangang
 */
public class GenericEntityFactoryForList<S extends Serializable, T extends Entity<S>> {
	private Join join;
	private Collection<T> entities;
	private BasicDao dao;

	/**
	 * load and assemble domain objects together.
	 * 
	 * @param join the join between domain objects.
	 * @param list list of the entities
	 * @param dao  the data access object
	 */
	public void build(Join join, List<Entity<?>> list, BasicDao dao) {
		this.join = join;
		this.entities = (Collection<T>) list;
		this.dao = dao;

		String joinType = join.getJoinType();
		if ("oneToOne".equals(joinType)) {
			loadOfOneToOne(join);
			return;
		}
		if ("manyToOne".equals(joinType)) {
			loadOfManyToOne(join);
			return;
		}
		if ("oneToMany".equals(joinType)) {
			loadOfOneToMany(join);
			return;
		}
		if ("manyToMany".equals(joinType)) {
			throw new OrmException("Don't support the many to many relation now!");
		}
	}

	/**
	 * load data of the one to one relation.
	 * 
	 * @param join the join information
	 */
	private void loadOfOneToOne(Join join) {
		if (join == null || entities == null || entities.isEmpty())
			return;
		List<S> ids = new ArrayList<>();
		for (T entity : entities)
			ids.add(entity.getId());
		String clazz = join.getClazz();
		Entity<S> template = EntityUtils.createEntity(clazz, null);
		List<Entity<?>> listOfEntitiesNeedJoin = dao.loadForList(ids, template);

		Map<S, Entity<?>> mapOfEntitiesNeedJoin = new HashMap<>();
		for (Entity<?> enj : listOfEntitiesNeedJoin)
			mapOfEntitiesNeedJoin.put((S) enj.getId(), enj);

		for (T entity : entities) {
			Entity<?> enj = mapOfEntitiesNeedJoin.get(entity.getId());
			setValueOfJoinToEntity(entity, enj);
		}
	}

	/**
	 * load data of the many to one relation.
	 * 
	 * @param join the join information
	 */
	@SuppressWarnings("unchecked")
	private void loadOfManyToOne(Join join) {
		if (join == null || entities == null || entities.isEmpty())
			return;
		String joinKey = join.getJoinKey();
		List<S> ids = new ArrayList<>();
		for (T entity : entities) {
			S id = (S) BeanUtils.getValueByField(entity, joinKey);
			ids.add(id);
		}
		String clazz = join.getClazz();
		Entity<S> template = EntityUtils.createEntity(clazz, null);
		List<Entity<?>> listOfEntitiesNeedJoin = dao.loadForList(ids, template);

		Map<S, Entity<?>> mapOfEntitiesNeedJoin = new HashMap<>();
		for (Entity<?> enj : listOfEntitiesNeedJoin)
			mapOfEntitiesNeedJoin.put((S) enj.getId(), enj);

		for (T entity : entities) {
			S id = (S) BeanUtils.getValueByField(entity, joinKey);
			Entity<?> enj = mapOfEntitiesNeedJoin.get(id);
			setValueOfJoinToEntity(entity, enj);
		}
	}

	/**
	 * load data of the one to many relation.
	 * 
	 * @param join the join information
	 */
	private void loadOfOneToMany(Join join) {
		if (join == null || entities == null || entities.isEmpty())
			return;
		String clazz = join.getClazz();
		Entity<S> template = EntityUtils.createEntity(clazz, null);
		String joinKey = join.getJoinKey();
		for (T entity : entities) {
			BeanUtils.setValueByField(template, joinKey, entity.getId());
			List<Entity<?>> listOfEntitiesNeedJoin = dao.loadAll(template);
			setValueOfJoinToEntity(entity, listOfEntitiesNeedJoin);
		}
	}

	/**
	 * set value of the join to the entity.
	 * 
	 * @param entity
	 * @param value  the value that need to join.
	 */
	private void setValueOfJoinToEntity(T entity, Entity<?> value) {
		String name = join.getName();
		BeanUtils.setValueByField(entity, name, value);
	}

	/**
	 * set value of the join to the entity.
	 * 
	 * @param entity
	 * @param list   the list that need to join.
	 */
	private void setValueOfJoinToEntity(T entity, List<Entity<?>> list) {
		String name = join.getName();
		BeanUtils.setValueByField(entity, name, list);
	}
}
