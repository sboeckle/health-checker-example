import React from 'react'
import {  Table } from "semantic-ui-react";

const Services = ({ services }) => {
  return (
    <Table celled padded>
        <Table.Header>
          <Table.Row>
            <Table.HeaderCell>ID</Table.HeaderCell>
            <Table.HeaderCell>Name</Table.HeaderCell>
            <Table.HeaderCell>URL</Table.HeaderCell>
            <Table.HeaderCell>Status</Table.HeaderCell>
            <Table.HeaderCell>MetaData</Table.HeaderCell>
          </Table.Row>
        </Table.Header>

        <Table.Body>
          {services.map(s => {
            return (
              <Table.Row key={s.id}>
                <Table.Cell>{s.id}</Table.Cell>
                <Table.Cell>{s.name}</Table.Cell>
                <Table.Cell>{s.url}</Table.Cell>
                <Table.Cell>{s.status}</Table.Cell>
                <Table.Cell>
                  <div>createdAt: {s.createdAt}</div>
                  <div>updatedAt: {s.updatedAt}</div>
                </Table.Cell>
              </Table.Row>
            );
          })}
        </Table.Body>
      </Table>
  )
};

export default Services