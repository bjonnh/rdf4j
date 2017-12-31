/*******************************************************************************
 * Copyright (c) 2016 Eclipse RDF4J contributors.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Distribution License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *******************************************************************************/

package org.eclipse.rdf4j.sail.shacl.AST;

import org.eclipse.rdf4j.common.iteration.Iterations;
import org.eclipse.rdf4j.model.Resource;
import org.eclipse.rdf4j.model.Statement;
import org.eclipse.rdf4j.model.vocabulary.SHACL;
import org.eclipse.rdf4j.repository.Repository;
import org.eclipse.rdf4j.sail.shacl.plan.PlanNode;
import org.eclipse.rdf4j.repository.RepositoryResult;
import org.eclipse.rdf4j.repository.sail.SailRepositoryConnection;
import org.eclipse.rdf4j.sail.shacl.ShaclSailConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Heshan Jayasinghe
 */
public class PropertyShape implements PlanGenerator, RequiresEvalutation {

	Resource id;

	Shape shape;


	public PropertyShape(Resource id, Shape shape) {
		this.id = id;
		this.shape = shape;
	}

	@Override
	public PlanNode getPlan(ShaclSailConnection shaclSailConnection, Shape shape) {
		throw new IllegalStateException("Should never get here!!!");
	}

	@Override
	public boolean requiresEvalutation(Repository addedStatements, Repository removedStatements) {
		return false;
	}

	static class Factory {

		static List<PropertyShape> getProprtyShapes(Resource ShapeId, SailRepositoryConnection connection, Shape shape) {

			try (Stream<Statement> stream = Iterations.stream(connection.getStatements(ShapeId, SHACL.PROPERTY, null))) {
				return stream
					.map(Statement::getObject)
					.map(v -> (Resource) v)
					.map(propertyShapeId -> {
						if (hasMinCount(propertyShapeId, connection)) {
							return new MinCountPropertyShape(propertyShapeId, connection, shape);
						}else {
							return null; // unsupported property shape type
						}
					})
					.filter(Objects::nonNull)
					.collect(Collectors.toList());
			}

		}


		private static boolean hasMinCount(Resource id, SailRepositoryConnection connection) {
			return connection.hasStatement(id, SHACL.MIN_COUNT, null, true);
		}
	}
}



