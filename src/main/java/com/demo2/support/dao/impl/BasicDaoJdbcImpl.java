/* 
 * Created by 2019年4月17日
 */
package com.demo2.support.dao.impl;

import java.io.Serializable;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;

import com.demo2.support.dao.BasicDao;
import com.demo2.support.dao.impl.mybatis.GenericDao;
import com.demo2.support.entity.Entity;
import com.demo2.support.exception.DaoException;

/**
 * The implement of BasicDao with Jdbc.
 * 
 * @author fangang
 */
public class BasicDaoJdbcImpl implements BasicDao {
	@Autowired
	private GenericDao dao;

	@Override
	public <T> void insert(T entity) {
		if (entity == null)
			throw new DaoException("The entity is null");
		DaoHelper helper = EntityHelper.readDataFromEntity(entity);
		try {
			dao.insert(helper.getTableName(), helper.getColumns(), helper.getValues());
		} catch (DataAccessException e) {
			throw new DaoException("error when insert entity", e);
		}
	}

	@Override
	public <T> void update(T entity) {
		if (entity == null)
			throw new DaoException("The entity is null");
		DaoHelper helper = EntityHelper.readDataFromEntity(entity);
		try {
			dao.update(helper.getTableName(), helper.getColMap(), helper.getPkMap());
		} catch (DataAccessException e) {
			throw new DaoException("error when update entity", e);
		}
	}

	@Override
	public <T> void insertOrUpdate(T entity) {
		if (entity == null)
			throw new DaoException("The entity is null");
		DaoHelper helper = EntityHelper.readDataFromEntity(entity);
		try {
			dao.insert(helper.getTableName(), helper.getColumns(), helper.getValues());
		} catch (DataAccessException e) {
			if (e.getCause() instanceof SQLIntegrityConstraintViolationException)
				update(entity);
			else
				throw new DaoException("error when insert entity", e);
		}
	}

	@Override
	public <T, S extends Collection<T>> void insertOrUpdateForList(S list) {
		for (Object entity : list)
			insertOrUpdate(entity);
	}

	@Override
	public <T> void delete(T entity) {
		DaoHelper helper = EntityHelper.readDataFromEntity(entity);
		dao.delete(helper.getTableName(), helper.getPkMap());
	}

	@Override
	public <T, S extends Collection<T>> void deleteForList(S list) {
		for (Object entity : list)
			delete(entity);
	}

	@Override
	public <S extends Serializable, T extends Entity<S>> void deleteForList(Collection<S> ids, T template) {
		DaoHelper helper = EntityHelper.prepareForList(ids, template);
		dao.deleteForList(helper.getTableName(), helper.getPkMap());
	}

	@Override
	public <S extends Serializable, T extends Entity<S>> T load(S id, T template) {
		if (id == null || template == null)
			throw new DaoException("illegal parameters!");
		template.setId(id);
		DaoHelper helper = EntityHelper.readDataFromEntity(template);
		List<Map<String, Object>> list = dao.find(helper.getTableName(), helper.getPkMap());
		if (list.isEmpty())
			return null;
		Map<String, Object> map = list.get(0);
		return EntityHelper.convertMapToEntity(map, template);
	}

	@Override
	public <S extends Serializable, T extends Entity<S>> List<Entity<?>> loadForList(Collection<S> ids, T template) {
		DaoHelper helper = EntityHelper.prepareForList(ids, template);

		List<Map<String, Object>> list = dao.load(helper.getTableName(), helper.getPkMap());

		// convert result set from List<Map> to List<Entity>
		List<Entity<?>> listOfEntity = new ArrayList<>();
		for (Map<String, Object> map : list) {
			@SuppressWarnings("unchecked")
			T temp = (T) template.clone();
			T entity = EntityHelper.convertMapToEntity(map, temp);
			listOfEntity.add(entity);
		}
		return listOfEntity;
	}

	@Override
	public <S extends Serializable, T extends Entity<S>> List<Entity<?>> loadAll(T template) {
		DaoHelper helper = EntityHelper.readDataFromEntity(template);
		List<Map<String, Object>> list = dao.find(helper.getTableName(), helper.getColMap());

		List<Entity<?>> listOfEntity = new ArrayList<>();
		for (Map<String, Object> map : list) {
			@SuppressWarnings("unchecked")
			T temp = (T) template.clone();
			T entity = EntityHelper.convertMapToEntity(map, temp);
			listOfEntity.add(entity);
		}
		return listOfEntity;
	}

	@Override
	public <S extends Serializable, T extends Entity<S>> void delete(S id, T template) {
		if (id == null || template == null)
			throw new DaoException("illegal parameters!");
		T entity = this.load(id, template);
		this.delete(entity);
	}
}
