import address_pb2
from google.protobuf.json_format import MessageToJson


person = address_pb2.Person()
person.name = 'Gilbert'
person.age = 30
person.email = 'gilbert.app@gmail.com'
print(person)

jsonObj = MessageToJson(person)
print(jsonObj)