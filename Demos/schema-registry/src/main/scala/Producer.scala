import java.io.ByteArrayOutputStream
import java.util.Properties

import io.confluent.kafka.schemaregistry.client.CachedSchemaRegistryClient
import org.apache.avro.Schema
import org.apache.avro.io.EncoderFactory
import org.apache.kafka.clients.producer.{KafkaProducer, ProducerRecord}
import org.apache.avro.reflect.{ReflectData, ReflectDatumWriter}

class SuperHero(nameParam: String, superPowersParam: String) {
  val name: String = nameParam
  val superPowers: String = superPowersParam
}

object Producer extends App {

  val props = new Properties()

  props.put("bootstrap.servers", "localhost:9092")
  props.put("acks", "all")
  props.put("retries", "0")
  props.put("key.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer")
  props.put("value.serializer", "io.confluent.kafka.serializers.KafkaAvroSerializer")
  props.put("schema.registry.url", "http://localhost:8081")

  // Hard coding topic too.
  val topic = "superheros"

  val schemaRegistry = new CachedSchemaRegistryClient("http://localhost:8081", 1000)

  val producer = new KafkaProducer[String, Array[Byte]](props)

  val superMan = new SuperHero("superman", "flying, super strength, underpants over clothes")
  val antMan = new SuperHero("antman", "getting small, sarcasm")

  val schema = ReflectData.get().getSchema(superMan.getClass)
  val id1 = schemaRegistry.register("DCSchema", schema)
  val id2 = schemaRegistry.register("MarvelSchema", schema)


  val record1 = new ProducerRecord[String, Array[Byte]](topic, "DCSchema", toBytes(superMan, schema))
  producer.send(record1)
  val record2 = new ProducerRecord[String, Array[Byte]](topic, "MarvelSchema", toBytes(antMan, schema))
  producer.send(record2)


  def toBytes[V](input: V, schema: Schema): Array[Byte] = {

    val result = new ByteArrayOutputStream()
    val writer = new ReflectDatumWriter[V](schema)
    val binEncoder = EncoderFactory.get().binaryEncoder(result, null)
    writer.write(input, binEncoder)
    binEncoder.flush()

    result.toByteArray
  }
}

